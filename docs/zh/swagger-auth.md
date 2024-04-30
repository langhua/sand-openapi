# Swagger用户认证

Swagger用户认证，是指在Swagger文档中定义用户认证方式，并实现相应的认证逻辑。

### 准备工作
把${ofbiz_home}/plugins/sand-openapi/docs/yaml复制到${ofbiz_home}/runtime/openapi/下，启动ofbiz，访问[https://localhost:8443/openapi/](https://localhost:8443/openapi/)，点击File菜单，可以看到yaml目录。

<br>

### 支持四种认证模式
[OpenAPI支持五种认证模式](https://swagger.io/docs/specification/authentication/)，Sand-Openapi模块实现了其中的四种，具体如下：

1. [Http基本认证](basic-authn.md)
2. [Http Bearer认证](bearer-authn.md)
3. [Cookie认证](cookie-authn.md)
4. [OAuth 2.0认证](oauth2.md)

<br>

### 参考文档
1. [OpenAPI支持五种认证模式](https://swagger.io/docs/specification/authentication/)
2. [Http基本认证(Basic Authentication)](https://swagger.io/docs/specification/authentication/basic-authentication/)
3. [Http Bearer认证](https://swagger.io/docs/specification/authentication/bearer-authentication/)
4. [Cookie Authentication](https://swagger.io/docs/specification/authentication/cookie-authentication/)
5. [OAuth 2.0](https://swagger.io/docs/specification/authentication/oauth2/)