package com.example.mybatplusdemo.service.impl;

import com.example.mybatplusdemo.service.SingPassService;
import com.example.mybatplusdemo.vo.SingpassUserInfoVo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDHDecrypter;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SingPassServiceImpl implements SingPassService {

    @Value("${myinfo.client-id}")
    private String clientId;

    @Value("${myinfo.redirect-uri}")
    private String redirectUri;

    @Value("${myinfo.private-key-path}")
    private String privateKeyPath;

    @Value("${myinfo.private-en-key-path}")
    private String privateEnKeyPath;

    // 存储 state -> codeVerifier 映射，用于 PKCE 校验
    private final Map<String, String> verifierStore = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        System.out.println("=== 初始化 MyInfoService ===");
        System.out.println("客户端ID: " + clientId);
    }

    // 生成登录链接，包含 PKCE 参数
    @Override
    public String generateLoginUrl() {
        String encryptionKid = "my-rsa-enc-key-2025"; // 与 JWKS 中的 kid 一致
        String state = UUID.randomUUID().toString();
        String nonce = UUID.randomUUID().toString();
        String codeVerifier = generateCodeVerifier();
        String codeChallenge;
        try {
            codeChallenge = generateCodeChallenge(codeVerifier);
        } catch (Exception e) {
            throw new RuntimeException("生成 PKCE code challenge 失败", e);
        }
        verifierStore.put(state, codeVerifier);
        String scope = "openid name uinfin dob nationality";
        return "https://stg-id.singpass.gov.sg/auth?" +
                "scope=" + scope +
                "&response_type=code" +
                "&redirect_uri=" + redirectUri +
                "&nonce=" + nonce +
                "&client_id=" + clientId +
                "&state=" + state +
                "&code_challenge=" + codeChallenge +
                "&code_challenge_method=S256"
                ;
    }

    // 处理授权回调，获取 access_token 并调用 userinfo 接口获取用户信息
    @Override
    public SingpassUserInfoVo handleCallback(String code, String state) {
        String codeVerifier = verifierStore.get(state);
        if (codeVerifier == null) {
            throw new RuntimeException("无效的 state，找不到对应的 codeVerifier");
        }
        verifierStore.remove(state);

        String accessToken = getAccessToken(code, codeVerifier);
        System.out.println("access_token:" + accessToken);
        return getUserInfo(accessToken);
    }

    // 读取 EC 私钥文件 PEM 字符串
    @SneakyThrows
    private String readPrivateKey() {
        Resource resource = new ClassPathResource(privateKeyPath);
        try (InputStream inputStream = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    // 生成 client_assertion JWT，ES256 签名
    @SneakyThrows
    public String generateClientAssertion() {
        String privateKeyPEM = readPrivateKey();

        ECKey ecJWK = (ECKey) ECKey.parseFromPEMEncodedObjects(privateKeyPEM);
        ECDSASigner signer = new ECDSASigner(ecJWK);

        long nowSec = System.currentTimeMillis() / 1000L;
        Date now = new Date(nowSec * 1000L);
        Date exp = new Date((nowSec + 5 * 60) * 1000L);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(clientId)
                .subject(clientId)
                .audience("https://stg-id.singpass.gov.sg/token")
                .issueTime(now)
                .expirationTime(exp)
                .jwtID(UUID.randomUUID().toString())
                .build();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                .type(JOSEObjectType.JWT)
//                .keyID(ecJWK.getKeyID())
                .keyID(ecJWK.getKeyID())
                .build();

        SignedJWT signedJWT = new SignedJWT(header, claims);
        signedJWT.sign(signer);

        String assertion = signedJWT.serialize();

        if (!signedJWT.verify(new com.nimbusds.jose.crypto.ECDSAVerifier(ecJWK.toPublicJWK()))) {
            throw new RuntimeException("Client Assertion 签名验证失败");
        }

        System.out.println("Client Assertion JWT:\n" + assertion);
        return assertion;
    }

    // 用授权码和 code_verifier 换取 access_token
    @SneakyThrows
    public String getAccessToken(String code, String codeVerifier) {
        OkHttpClient client = new OkHttpClient();

        String clientAssertion = generateClientAssertion();

        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("code", code)
                .add("redirect_uri", redirectUri)
                .add("client_id", clientId)
                .add("code_verifier", codeVerifier)
                .add("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer")
                .add("client_assertion", clientAssertion)
                .build();

        Request request = new Request.Builder()
//                .url("https://stg-id.singpass.gov.sg/token")
                .url("https://stg-id.singpass.gov.sg/token")
                .post(formBody)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String respBody = response.body().string();
            System.out.println("Token Endpoint 响应: " + respBody);
            JSONObject json = new JSONObject(respBody);

            if (!json.has("access_token")) {
                throw new RuntimeException("获取 access_token 失败: " + json.optString("error_description", "未知错误"));
            }

            return json.getString("access_token");
//            return json.getString("id_token");
        }
    }

    // 使用 access_token 调用标准 /userinfo 接口获取用户信息
    @SneakyThrows
    public SingpassUserInfoVo getUserInfo(String accessToken) {
        OkHttpClient client = new OkHttpClient();
//
        Request request = new Request.Builder()
                .url("https://stg-id.singpass.gov.sg/userinfo")
                .get()
                .header("Authorization", "Bearer " + accessToken)
//                .header("Accept", "application/json")
                .header("Accept", "application/jwt") // 要求返回JWT格式

                .build();

        try (Response response = client.newCall(request).execute()) {
            String respBody = response.body().string();
            System.out.println("/userinfo 原始响应（JWT）: " + respBody);

            // 1️⃣ 解析加密的 JWT（JWE）
            JWEObject jweObject = JWEObject.parse(respBody);

            // 2️⃣ 构造私钥（你已有 readPrivateKey 方法）
            String privateKeyPem = readEnPrivateKey(); // 从文件或类路径读取私钥（PEM格式）
            ECPrivateKey privateKey = getPrivateKeyFromPem(privateKeyPem); // 解析为 EC 私钥

            // 3️⃣ 解密 JWE
            jweObject.decrypt(new ECDHDecrypter(privateKey));

            // 4️⃣ 解密后得到 SignedJWT
            SignedJWT signedJWT = jweObject.getPayload().toSignedJWT();

            // 5️⃣ 提取 claims（JSON 对象）
            Map<String, Object> claimsMap = signedJWT.getJWTClaimsSet().toJSONObject();
            String json = new ObjectMapper().writeValueAsString(claimsMap);
            System.out.println("/userinfo 解密后的 JSON: " + json);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(json);
            String name = rootNode.path("name").path("value").asText();
            System.out.println("Name = " + name);
            String uinfin = rootNode.path("uinfin").path("value").asText();
            System.out.println("uinfin = " + uinfin);
            String nationality = rootNode.path("nationality").path("value").asText();
            System.out.println("nationality = " + nationality);
            String dob = rootNode.path("dob").path("value").asText();
            System.out.println("dob = " + dob);
            String sub = rootNode.path("sub").asText();
            String uuid = sub.replace("u=", ""); // 去掉前缀“u=”
            System.out.println("uuid = " + uuid);
            SingpassUserInfoVo singpassUserInfoVo = new SingpassUserInfoVo();
            singpassUserInfoVo.setName(name);
            singpassUserInfoVo.setUinfin(uinfin);
            singpassUserInfoVo.setNationality(nationality);
            singpassUserInfoVo.setDob(dob);
            singpassUserInfoVo.setSub(sub);
            return singpassUserInfoVo;
        }
    }

    // 生成随机的 PKCE code_verifier
    public static String generateCodeVerifier() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // 根据 code_verifier 生成 PKCE code_challenge（SHA256 + base64url）
    public static String generateCodeChallenge(String codeVerifier) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }

    @SneakyThrows
    public String decryptJwe(String jweString) {
        // 读取私钥 PEM
        String privateKeyPEM = readEnPrivateKey();

        // 解析 EC 私钥（和生成 client_assertion 用同一把）
        ECKey ecJWK = (ECKey) ECKey.parseFromPEMEncodedObjects(privateKeyPEM);

        // 创建 JWE 对象
        JWEObject jweObject = JWEObject.parse(jweString);

        // 用私钥构造解密器
        ECDHDecrypter decrypter = new ECDHDecrypter(ecJWK);

        // 解密
        jweObject.decrypt(decrypter);

        // 获取解密后的负载（明文）
        return jweObject.getPayload().toString();
    }

    @SneakyThrows
    private String readEnPrivateKey() {
        Resource resource = new ClassPathResource(privateEnKeyPath);
        try (InputStream inputStream = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    public ECPrivateKey getPrivateKeyFromPem(String pem) throws Exception {
        // 去除 PEM 文件头尾和换行符等非 Base64 字符
        String privateKeyPEM = pem
                .replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("-----END (.*)-----", "")
                .replaceAll("\\s", "");  // 移除所有空白字符（包括换行）

        byte[] decoded = Base64.getDecoder().decode(privateKeyPEM); // Base64 解码
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("EC"); // MyInfo 使用 EC 密钥
        return (ECPrivateKey) keyFactory.generatePrivate(keySpec);
    }

}
