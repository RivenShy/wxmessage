package com.example.mybatplusdemo.controller;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

public class EcJwkGeneratorFromClasspath {

    public static void main(String[] args) throws Exception {
//        // 加载 classpath 下的 ec_public.pem
//        InputStream is = EcJwkGeneratorFromClasspath.class
//                .getClassLoader()
//                .getResourceAsStream("keys/ec_public.pem");
//
//        if (is == null) {
//            throw new RuntimeException("找不到 ec_public.pem 文件，请确认路径为 resources/keys/ec_public.pem");
//        }
//
//        // 读取并清理 PEM 内容
//        byte[] rawBytes = readAllBytesJava8(is);
//        String pem = new String(rawBytes)
//                .replace("-----BEGIN PUBLIC KEY-----", "")
//                .replace("-----END PUBLIC KEY-----", "")
//                .replaceAll("\\s", "");
//
//        byte[] decoded = Base64.getDecoder().decode(pem);
//
//        // 转换为 ECPublicKey
//        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
//        KeyFactory keyFactory = KeyFactory.getInstance("EC");
//        ECPublicKey ecPublicKey = (ECPublicKey) keyFactory.generatePublic(spec);
//
//        // 生成 EC JWK（公钥）
//        ECKey ecJWK = new ECKey.Builder(Curve.P_256, ecPublicKey)
//                .keyUse(KeyUse.SIGNATURE)
//                .algorithm(JWSAlgorithm.ES256)
//                .keyID("my-ec-key-2025") // 与 JWT header 中 kid 一致
//                .build();
//
//        // 输出 JWK JSON（公钥部分）
//        JWKSet jwkSet = new JWKSet(ecJWK);
//        JSONObject jsonObject = new JSONObject(jwkSet.toJSONObject());
//        String jwkJson = jsonObject.toString(2); // 缩进2个空格，美化格式
//
//        System.out.println("生成的 EC 公钥 JWK（用于上传 Singpass JWKS）：\n" + jwkJson);

        String jwkJson = generateJwksFromPublicKey();
        System.out.println("生成的 JWK：\n" + jwkJson);
    }

    private static byte[] readAllBytesJava8(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = input.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }


    public static String generateJwksFromPublicKey() throws IOException, JOSEException {
        // 读取签名公钥（ES256）
        Resource sigResource = new ClassPathResource("keys/ec_public.pem");
        String sigPublicKeyPEM = readPemFile(sigResource);
        ECKey ecKeySig = (ECKey) ECKey.parseFromPEMEncodedObjects(sigPublicKeyPEM);

        // 构建签名用 JWK
        ECKey sigPublicJWK = new ECKey.Builder(Curve.P_256, ecKeySig.getX(), ecKeySig.getY())
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.ES256)
                .keyID("my-ec-sig-key-2025")
                .build();

        // 读取加密公钥（ECDH-ES+A256KW）
        Resource encResource = new ClassPathResource("keys/ec_en_public.pem");
        String encPublicKeyPEM = readPemFile(encResource);
        ECKey ecKeyEnc = (ECKey) ECKey.parseFromPEMEncodedObjects(encPublicKeyPEM);

        // 构建加密用 JWK
        ECKey encPublicJWK = new ECKey.Builder(Curve.P_256, ecKeyEnc.getX(), ecKeyEnc.getY())
                .keyUse(KeyUse.ENCRYPTION)
                .algorithm(JWEAlgorithm.ECDH_ES_A256KW)
                .keyID("my-ec-enc-key-2025")
                .build();

        // 合并为 JWKS
        JWKSet jwkSet = new JWKSet(Arrays.asList(
                sigPublicJWK.toPublicJWK(),
                encPublicJWK.toPublicJWK()
        ));

        return jwkSet.toString();
    }



    private static String readPemFile(Resource resource) throws IOException {
        try (InputStream is = resource.getInputStream()) {
            return new BufferedReader(new InputStreamReader(is))
                    .lines().collect(Collectors.joining("\n"));
        }
    }
}
