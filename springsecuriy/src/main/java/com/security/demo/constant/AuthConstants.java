package com.security.demo.constant;

public interface AuthConstants extends TokenConstant {
    String CAPTCHA_HEADER_KEY = "Captcha-Key";
    String CAPTCHA_HEADER_CODE = "Captcha-Code";
    String CAPTCHA_NOT_CORRECT = "验证码不正确";
    String TENANT_PARAM_KEY = "tenant_id";
    String TENANT_HEADER_KEY = "Tenant-Id";
    String TENANT_NOT_FOUND = "租户ID未找到";
    String USER_NOT_FOUND = "用户名或密码错误";
    String USER_HAS_NO_ROLE = "未获得用户的角色信息";
    String USER_HAS_NO_TENANT = "未获得用户的租户信息";
    String USER_HAS_NO_TENANT_PERMISSION = "租户授权已过期,请联系管理员";
    String HEADER_KEY = "Authorization";
    String HEADER_PREFIX = "Basic ";
    String CAPTCHA_CODE_CACHE_KEY = "CAPTCHA_CODE:";
    String CLIENT_CREDENTIALS = "client_credentials";
    String CODE = "code";
    String DEFAULT_TENANT_ID = "000000";
    String USER_HAS_TOO_MANY_FAILS = "登录错误次数过多,请稍后再试";
    String PASSWORD_KEY = "password";
    String GRANT_TYPE_KEY = "grant_type";
    String REFRESH_TOKEN_KEY = "refresh_token";
    String REFRESH_TIME = "refresh_time";
}
