package com.security.demo.util;


import com.security.demo.constant.AuthConstants;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class AuthUtils {

    public static String getRequestTenantId() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String tenantId = request.getHeader(AuthConstants.TENANT_HEADER_KEY);
        if (StringUtils.isEmpty(tenantId)) {
            tenantId = request.getParameter(AuthConstants.TENANT_PARAM_KEY);
        }
        return StringUtils.isEmpty(tenantId) ? AuthConstants.DEFAULT_TENANT_ID : tenantId;
    }

    private static byte[] encodeHex(byte[] data) {
        int len = data.length;
        byte[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        byte[] out = new byte[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            out[j++] = digits[(0xF0 & data[i]) >>> 4];
            out[j++] = digits[0xF & data[i]];
        }
        return out;
    }

    private static byte[] des(byte[] data, byte[] desKey, int mode) {
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            Cipher cipher = Cipher.getInstance("DES");
            DESKeySpec desKeySpec = new DESKeySpec(desKey);
            cipher.init(mode, keyFactory.generateSecret(desKeySpec), new SecureRandom());
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new AccessDeniedException("密码错误");
        }
    }

    public static String decryptFormHex(@Nullable String data, String password) {
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        byte[] hexBytes = encodeHex(data.getBytes(StandardCharsets.UTF_8));
        byte[] result = des(hexBytes, password.getBytes(StandardCharsets.UTF_8), Cipher.DECRYPT_MODE);
        return new String(result, StandardCharsets.UTF_8);
    }

//	public static void main(String[] args) {
//		System.out.println(Base64.getEncoder().encodeToString("wxmp:wxMp091235".getBytes(StandardCharsets.UTF_8)));
//	}


}
