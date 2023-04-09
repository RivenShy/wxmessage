# 使用指南

## 一、准备工作
1. 执行auth-center/docs/sql/init.sql 文件，创建数据库并创建相关的表
2. 修改auth-center项目下的配置文件中的数据库连接配置

## 二、接口测试
运行 AuthCenterApplication 程序，测试几种oauth认证模式

### 1. 授权码认证模式
> 最安全的一种模式。一般用于client是Web服务器端应用或第三方的原生App调用资源服务的时候。因为在这种模式中access_token不会经过浏览器或移动端的App，而是直接从服务端去交换，这样就最大限度的减小了令牌泄漏的风险。
> 该模式下获取token需要分两步走，第一步获取授权码，第二步获取token。

- 获取授权码

![授权码模式-获取授权码](../git-imgs/授权码模式%20-%20获取授权码.gif)

**接口地址** `http://127.0.0.1:30000/oauth/authorize`
**请求方式** `GET`
**请求参数**

|  字段名 | 描述  |
| ------------ | ------------ |
| client_id | 改值必须和配置在clients中的值保持一致|
| response_type |固定传值`code`表示使用授权码模式进行认证|
| scope | 改值必须配置的clients中的值一致 |
| redirect_uri | 获取code之后重定向的地址，必须和配置的clients一致 |

**请求示例**

http://127.0.0.1:30000/oauth/authorize?client_id=c1&response_type=code&scope=all&redirect_uri=https://www.baidu.com

账号密码分别输入：zhangsan/123，进入授权页面之后点击授权按钮，页面跳转之后获取到code。

- 获取token

在上一步获取到code之后，利用获取到的该code获取token。

**接口地址** `http://127.0.0.1:30000/oauth/token`
**请求方式** `POST`
**请求参数**

|  字段名 | 描述  |
| ------------ | ------------ |
|code|上一步获取到的code|
|grant_type|在授权码模式，固定使用`authorization_code`|
| client_id | 改值必须和配置在clients中的值保持一致|
|client_secret|这里的值必须和代码中配置的clients中配置的保持一致|
| redirect_uri | 获取token之后重定向的地址，该地址可以随意写 |

**请求示例**

http://127.0.0.1:30000/oauth/token

请求体
``` x-www-form-urlencoded
code:5Rmc3m
grant_type:authorization_code
client_id:c1
client_secret:secret
redirect_uri:https://www.baidu.com
```

### 2.简化模式
> 该模式去掉了授权码，用户登陆之后直接获取token并显示在浏览器地址栏中，参数和请求授权码的接口基本上一模一样，唯一的区别就是`response_type`字段，授权码模式下使用的是code字段，在简化模式下使用的是token字段。一般来说，简化模式用于没有服务器端的第三方单页面应用，因为没有服务器端就无法接收授权码。

**接口地址** `http://127.0.0.1:30000/oauth/authorize`
**请求方式** `GET`
**请求参数**

|  字段名 | 描述  |
| ------------ | ------------ |
| client_id | 改值必须和配置在clients中的值保持一致|
| response_type |固定传值`token`表示使用简化模式进行认证|
| scope | 该值必须和配置的clients中的值一致 |
| redirect_uri | 获取code之后重定向的地址，必须和配置的clients一致 |

**请求示例**

http://127.0.0.1:30000/oauth/authorize?client_id=c1&response_type=token&scope=all&redirect_uri=https://www.baidu.com

### 3.密码模式
> 这种模式十分简单，但是却意味着直接将用户敏感信息泄漏给了client，因此这就说明这种模式只能用于client是我
  们自己开发的情况下。因此密码模式一般用于我们自己开发的，第一方原生App或第一方单页面应用


**接口地址** `http://127.0.0.1:30000/oauth/token`
**请求方式** `POST`
**请求参数**

|  字段名 | 描述  |
| ------------ | ------------ |
| client_id | 改值必须和配置在clients中的值保持一致|
| client_secret |改值必须和配置在clients中的值保持一致|
| grant_type | 在密码模式下，该值固定为`password` |
| username | 用户名 |
| password | 密码 |

**请求示例**

http://127.0.0.1:30000/oauth/token?client_id=c1&client_secret=secret&grant_type=password&username=zhangsan&password=123

### 4.客户端模式

> 这种模式是最方便但最不安全的模式。因此这就要求我们对client完全的信任，而client本身也是安全的。因此这种模式一般用来提供给我们完全信任的服务器端服务。比如，合作方系统对接，拉取一组用户信息。

**接口地址** `http://127.0.0.1:30000/oauth/token`
**请求方式** `POST`
**请求参数**

|  字段名 | 描述  |
| ------------ | ------------ |
| client_id | 改值必须和配置在clients中的值保持一致|
| client_secret |改值必须和配置在clients中的值保持一致|
| grant_type | 在密码模式下，该值固定为`client_credentials` |

**请求示例**

http://127.0.0.1:30000/oauth/token?client_id=c1&client_secret=secret&grant_type=client_credentials

### 5.refresh_token换取新token

**接口地址** `http://127.0.0.1:30000/oauth/token`
**请求方式** `POST`
**请求参数**

|  字段名 | 描述  |
| ------------ | ------------ |
| client_id | 该值必须和配置在clients中的值保持一致|
| client_secret |该值必须和配置在clients中的值保持一致|
| grant_type | 如果想根据refresh_token换新的token，这里固定传值`refresh_token` |
| refresh_token | 未失效的refresh_token |


**请求示例**

http://127.0.0.1:30000/oauth/token?grant_type=refresh_token&refresh_token=09c9d11a-525a-4e5f-bac1-4f32e9025301&client_id=c1&client_secret=secret
