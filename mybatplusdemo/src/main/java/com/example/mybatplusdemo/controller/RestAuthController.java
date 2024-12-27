package com.example.mybatplusdemo.controller;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.xkcoding.http.config.HttpConfig;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.request.AuthGoogleRequest;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.aspectj.apache.bcel.classfile.Utility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Collections;


@RestController
@RequestMapping("/oauth")
public class RestAuthController {
    @Value("${google.client-id}")
    private  String clientid;
    @Value("${google.client-secret}")
    private  String clientsecret;
    @Value("${google.redirect-uri}")
    private  String  redirecturi;


    @RequestMapping("/render")
    public void renderAuth(HttpServletResponse response) throws IOException {
        AuthRequest authRequest = getAuthRequest();
        response.sendRedirect(authRequest.authorize(AuthStateUtils.createState()));
    }

    @RequestMapping("/callback")
    public Object login(AuthCallback callback) {
        AuthRequest authRequest = getAuthRequest();
        AuthResponse authResponse = authRequest.login(callback);
        return authResponse;
    }

//    @RequestMapping(value = "/callback")
//    public Utility.ResultHolder googleLogin(AuthCallback callback){
//        String code = callback.getCode();
//        Utility.ResultHolder result = null;
//        try {
//            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
//                    .setAudience(Collections.singletonList(clientid))
//                    .build();
//            GoogleIdToken idToken = verifier.verify(code);
//            if (idToken != null){
//                GoogleIdToken.Payload payload = idToken.getPayload();
//                String email = payload.getEmail();
//                String name = (String) payload.get("name");
//                //处理流程
//            }
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
//        return result;
//    }

    private AuthRequest getAuthRequest() {
        return new AuthGoogleRequest(AuthConfig.builder()
                .clientId(clientid)
                .clientSecret(clientsecret)
                .redirectUri(redirecturi)
                .httpConfig(HttpConfig.builder()
                        .timeout(15000)
//                        //proxy host and port
////                        .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 1087)))
                        .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 10810)))

//                        .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 61490)))
//
                        .build())
                .build());
    }
}