package com.example.mybatplusdemo.controller;

import com.example.mybatplusdemo.service.SingPassService;
import com.example.mybatplusdemo.utils.R;
import com.example.mybatplusdemo.utils.StringUtil;
import com.example.mybatplusdemo.vo.SingpassUserInfoVo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Base64;

@RestController
@RequiredArgsConstructor
@RequestMapping("/singpass")
public class SingPassController {

    private final SingPassService singPassService;
    private static final Logger logger = LoggerFactory.getLogger(SingPassController.class);

    @GetMapping("/auth")
    public void auth(HttpServletResponse response) throws IOException {
        String loginUrl = singPassService.generateLoginUrl();
        response.sendRedirect(loginUrl);
    }

    @GetMapping("/getAuthUrl")
    public String getAuthUrl(HttpServletResponse response) throws IOException {
        return singPassService.generateLoginUrl();
    }

    @GetMapping("/callback")
    public R<SingpassUserInfoVo> callback(@RequestParam(value = "code", required = false) String code, @RequestParam(value = "state", required = false) String state) throws ServletException {
        logger.info("Callback received:");
        logger.info("  code = {}", code);
        logger.info("  state = {}", state);
        if(StringUtil.isBlank(code) || StringUtil.isBlank(state)) {
            throw new ServletException("获取singpass用户信息失败！");
        }
        try {
            SingpassUserInfoVo personData = singPassService.handleCallback(code, state);
//            logger.info("Decrypted person data: {}", personData);
            return R.data(personData);
        } catch (Exception e) {
            logger.error("Error during Singpass callback handling", e);
//            throw new ServletException("Error during Singpass callback handling" + e.getMessage());
            return R.data(new SingpassUserInfoVo());
        }
    }

    public static void main(String[] args) throws Exception {
        String modulusBase64 = "p5j3pV8YTIiN8VpGkzqIzuaP3I2mUlfvIOlhFThRuTubhqi2vAB9sIAuHmX-q0nibY36ptWrtIjuhTM3kPOAF-raqtdr-O822c1AEkeRWUUkFsufXCD_VRYUcBzob2bTuAldsnc8wbk5FJx4HCGNI50QrTXgA2FV9tWFA93FCATsMMLtREy3qbSE6dC3p8BnAsaK9YO3sVT-4PD56YFCFdqXz4GBVAvpLsr9_2TnvnK6nJDbP9P77eSSkOId3cWacQkOzgWqqXtByy6aImplPb0lA1_b-shKfpy6-4DBwSQXWN9l3d2k-ppBNXLNgTEymWGmwuY3FcrN4waYBhGGdw";
        byte[] nBytes = Base64.getUrlDecoder().decode(modulusBase64);

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(nBytes);

        System.out.println("MD5: " + bytesToHex(digest));
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
