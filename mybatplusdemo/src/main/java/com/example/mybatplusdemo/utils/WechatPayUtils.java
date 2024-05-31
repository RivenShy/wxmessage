package com.example.mybatplusdemo.utils;

import com.example.mybatplusdemo.config.WechatUrlConfig;
import com.example.mybatplusdemo.config.WxpayConfig;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class WechatPayUtils {
    /**
     * 获取私钥
     * @param filename 私钥文件路径  (required)
     * @return 私钥对象
     */
    public static PrivateKey getPrivateKey(String filename) throws IOException {
        System.out.println("filename:" + filename);
        String content = new String(Files.readAllBytes(Paths.get(filename)), "utf-8");
        try {
            String privateKey = content.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("当前Java环境不支持RSA", e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("无效的密钥格式");
        }
    }

    /**
     * 生成token 也就是生成签名
     *
     * @param method
     * @param url
     * @param body
     * @return
     * @throws Exception
     */
    public static String getToken(String method, URL url, String body) throws Exception {
        String nonceStr = getNonceStr();
        long timestamp = System.currentTimeMillis() / 1000;
        String message = buildMessage(method, url, timestamp, nonceStr, body);
        String signature = sign(message.getBytes("utf-8"));

        return "WECHATPAY2-SHA256-RSA2048 " + "mchid=\"" + WxpayConfig.mch_id + "\","
                + "nonce_str=\"" + nonceStr + "\","
                + "timestamp=\"" + timestamp + "\","
                + "serial_no=\"" + WxpayConfig.mchSerialNo + "\","
                + "signature=\"" + signature + "\"";
    }


    /**
     * 获取平台证书
     *
     * @return
     */
    public static Map<String, X509Certificate> refreshCertificate() throws Exception {
        Map<String, X509Certificate> certificateMap = new HashMap();
        // 1: 执行get请求
        JsonNode jsonNode = HttpUtils.doGet(WechatUrlConfig.CERTIFICATESURL);
        // 2: 获取平台验证的相关参数信息
        JsonNode data = jsonNode.get("data");
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                JsonNode encrypt_certificate = data.get(i).get("encrypt_certificate");
                //对关键信息进行解密
                AesUtil aesUtil = new AesUtil(WxpayConfig.v3Key.getBytes());
                String associated_data = encrypt_certificate.get("associated_data").toString().replaceAll("\"", "");
                String nonce = encrypt_certificate.get("nonce").toString().replaceAll("\"", "");
                String ciphertext = encrypt_certificate.get("ciphertext").toString().replaceAll("\"", "");
                //证书内容
                String certStr = aesUtil.decryptToString(associated_data.getBytes(), nonce.getBytes(), ciphertext);
                //证书内容转成证书对象
                CertificateFactory cf = CertificateFactory.getInstance("X509");
                X509Certificate x509Cert = (X509Certificate) cf.generateCertificate(
                        new ByteArrayInputStream(certStr.getBytes("utf-8"))
                );
                String serial_no = data.get(i).get("serial_no").toString().replaceAll("\"", "");
                certificateMap.put(serial_no, x509Cert);
            }
        }
        return certificateMap;
    }


    /**
     * 生成签名
     *
     * @param message
     * @return
     * @throws Exception
     */
//    public static String sign(byte[] message) throws Exception {
//        Signature sign = Signature.getInstance("SHA256withRSA");
//        sign.initSign(getPrivateKey(WxpayConfig.KeyPath));
//        sign.update(message);
//        return Base64.getEncoder().encodeToString(sign.sign());
//    }

    /**
     * 生成签名串
     *
     * @param method
     * @param url
     * @param timestamp
     * @param nonceStr
     * @param body
     * @return
     */
    public static String buildMessage(String method, URL url, long timestamp, String nonceStr, String body) {
        String canonicalUrl = url.getPath();
        if (url.getQuery() != null) {
            canonicalUrl += "?" + url.getQuery();
        }
        return method + "\n"
                + canonicalUrl + "\n"
                + timestamp + "\n"
                + nonceStr + "\n"
                + body + "\n";
    }

    /**
     * 生成随机数
     *
     * @return
     */
//    public static String getNonceStr() {
//        return UUID.randomUUID().toString()
//                .replaceAll("-", "")
//                .substring(0, 32);
//    }

//

    /**
     * 验证签名
     *
     * @param certificate
     * @param message
     * @param signature
     * @return
     */
    public static boolean verify(X509Certificate certificate, byte[] message, String signature) {
        try {
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initVerify(certificate);
            sign.update(message);
            return sign.verify(Base64.getDecoder().decode(signature));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("当前Java环境不支持SHA256withRSA", e);
        } catch (SignatureException e) {
            throw new RuntimeException("签名验证过程发生了错误", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("无效的证书", e);
        }
    }


    /**
     * 生成随机数
     *
     * @return
     */
    public static String getNonceStr() {
        return UUID.randomUUID().toString()
                .replaceAll("-", "")
                .substring(0, 32);
    }


    /**
     * 拼接参数
     *
     * @return
     */

    public static String buildMessageTwo(String appId, long timestamp, String nonceStr, String packag) {
        return appId + "\n"
                + timestamp + "\n"
                + nonceStr + "\n"
                + packag + "\n";
    }


    /**
     * 生成签名
     *
     * @return
     */

    public static String sign(byte[] message) throws NoSuchAlgorithmException, SignatureException, IOException, InvalidKeyException {
        Signature sign = Signature.getInstance("SHA256withRSA"); //SHA256withRSA
        sign.initSign(WechatPayUtils.getPrivateKey(WxpayConfig.KeyPath));
        sign.update(message);
        return Base64.getEncoder().encodeToString(sign.sign());
    }


}