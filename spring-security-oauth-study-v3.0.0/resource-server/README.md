# 资源服务器

请求该服务接口必须带着令牌，请求顺序如下

## 1.请求[auth-center](../auth-center)服务获取token

例如：

POST请求：http://127.0.0.1:30000/oauth/token?client_id=c1&client_secret=secret&grant_type=password&username=zhangsan&password=123

得到响应结果：
```json
{
    "access_token": "11c5eaec-768f-400a-85e1-e2b52276b83d",
    "token_type": "bearer",
    "refresh_token": "34eb5d57-de7e-4f26-b35e-64162c64117e",
    "expires_in": 7199,
    "scope": "all"
}
```

## 2.请求 [resource-server](#)服务验证请求
GET请求：http://127.0.0.1:30001/r1
带着token：

|header|value|
| --- | --- |
|Authorization|Bearer 11c5eaec-768f-400a-85e1-e2b52276b83d|

得到响应结果：
```text
访问资源r1
```

错误的响应结果：
```json
{
    "error": "invalid_token",
    "error_description": "0cc2da26-b634-4ccb-a8fe-14f454a13090"
}
```

