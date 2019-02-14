## 基本概念
OAuth2模型有四个基本的角色

 * Resource Owner - 用户，资源所有者
 * Client - 应用服务端
 * Resource Server - 资源服务
 * Authorization Server - 授权服务

以[GitHub](https://github.com/) + [C9](https://c9.io/)为例，C9登录时选择Sign in with GitHub，对应关系为

 * GitHub - Authorization Server + Resource Server
 * C9 - Client
 * tomowang - Resource Owner

其中，Resource Server 提供的资源主要包括用户名、头像、邮箱等。对于认证过的应用，GitHub会记录应用的名称和授予的权限，具体可查看[https://github.com/settings/applications](https://github.com/settings/applications)

四种授权模型

 * Authorization Code - 授权码，一般用于服务端应用
 * Implicit - 隐式授权，一般用于移动应用和纯网页应用
 * Resource Owner Password Credentials - 直接密码授权，用户在Client处直接提交密码，一般情况下Client本身是服务的一部分
 * Client Credentials - 客户端应用授权，该模型无用户参与，客户端向Authorization Server发送自身的`clientId`及`clientSecret`进行授权

其中`Authorization Code`模型流程图如下

```
+------------+
|  Resource  |
|   Owner    |
+---+----^---+
    |    |
  A |    |B
    |    |                                       +--------------------+
+---v----+---+   A. User Authorization Request   |                    |
|            +----------------------------------->                    |
|            |       B. User Authorized          |                    |
|   Browser  +----------------------------------->    Authorization   |
|            |     C. Authorization Code         |       Server       |
|            <-----------------------------------+                    |
+-^--------+-+                                   |                    |
  |        |                                     +-----^--------+-----+
 A|        |C                                          |        |
  |        |                                           |        |
+-+--------v-+       D. Access Token Request           |        |
|            +-----------------------------------------+        |
|   Client   |       E. Access Token Grant                      |
|            <--------------------------------------------------+
+------------+
```

 * A - User access browser, browser redirect to authorization server
 * B - User approve/deny authorization request
 * C - When authorized, authorization code is returned to browser. Browser will send code to Client backend server.
 * D - Client backend server send authorization code to authorization server (with redirect URI)
 * E - AUthorization Server will check the code and redirect URI, and return access token.

## POC
该POC项目中包括3个模块

 * `authserver:8080` - Authorization Server + Resource Server
 * `client:9999` - Client
 * `resource:9090` - Client + Resource Server

使用到两种授权模型：Authorization Code + Client Credentials。其中client通过SSO进行认证，使用`Authorization Code`授权模式。访问`/api/demo`接口时，会转发至resource，使用`Client Credentials`授权模式。

### Database

```sql
create database authserver;
GRANT ALL PRIVILEGES ON authserver.* TO 'admin'@'127.0.0.1' IDENTIFIED BY 'secret';
FLUSH PRIVILEGES;
```

### Auth Server (Authorization + Resource)
主要配置

 * MVC - `com.example.authserver.config.WebMvcConfiguration`, view的渲染
 * WebSecurity - `com.example.authserver.config.WebSecurityConfiguration`, form表单登录
 * Autorization - `com.example.authserver.config.AuthorizationServerConfiguration`, authorization server 主要配置
   + `authenticationManager` - 用户表单登录认证
   + `tokenStore` - access token存储，目前用的是JWT
   + `accessTokenConverter` - JWT转换
   + `passwordEncoder` - 密码算法，用于user的密码校验和`clientId` + `clientSecret`校验
   + `EnhancedUserAuthenticationConverter` - 用于将用户类转成JWT中的JSON对象
   + 相关配置

   ```java
	// permission config for tokenKey - `/oauth/token_key`
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security)
	        throws Exception {
	    security
	            .passwordEncoder(passwordEncoder())
	            .tokenKeyAccess("permitAll()") // default is denyAll()
	            .checkTokenAccess("isAuthenticated()");
	}

	// endpoint
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints)
	        throws Exception {
	    endpoints
	            .authenticationManager(authenticationManager())
	            .tokenStore(tokenStore()).accessTokenConverter(accessTokenConverter())
	            .approvalStoreDisabled();
	}

	// client credentials
	@Override
	public void configure(ClientDetailsServiceConfigurer clients)
	        throws Exception {
	    clients.jdbc(dataSource).passwordEncoder(passwordEncoder());
	}
   ```

 * Resource - `com.example.authserver.config.ResourceServerConfiguration`, resource配置，auth server提供的唯一resource为`userinfo`接口

### Resource Server (Resource + Client)
Resource Server在该POC中指提供了一个接口

```java
@PreAuthorize("hasRole('ROLE_CLIENT')")
@RequestMapping(value = "/", method = RequestMethod.POST)
public boolean check(@RequestBody String message) {
	logger.info(message);
	return true;
}
```

`PreAuthorize`用来配置权限，可以使用`hasRole('ROLE_CLIENT')`或者`#oauth2.hasScope('read')`

### Client
Client配置了SSO用于OAuth2的认证，接口`/api/demo`受认证保护。同时该接口会请求Resource Server，以演示Client Credentials认证模式


```java
@RequestMapping(value = "/api/demo", method = RequestMethod.GET)
public Map<String, Boolean> demo() throws URISyntaxException {
	String message = "demo";
	Boolean result = resourceServerProxy.postForObject("http://127.0.0.1:9090", message, Boolean.class);
	return Collections.singletonMap("message", result);
}
```

其中`resourceServerProxy`的配置如下

```java
@Bean
@ConfigurationProperties("security.oauth2.client")
public ClientCredentialsResourceDetails getClientCredentialsResourceDetails() {
	return new ClientCredentialsResourceDetails();
}

@Bean
public OAuth2RestTemplate restTemplate() {
	AccessTokenRequest atr = new DefaultAccessTokenRequest();
	return new OAuth2RestTemplate(getClientCredentialsResourceDetails(),
			new DefaultOAuth2ClientContext(atr));
}
```

### Demo

 * 启动 auth server `cd authserver; gradle bootRun`
 * 启动 resource server `cd resource; gradle bootRun`
 * 启动 client `cd client; gradle bootRun`

auth server 必须先启动，原因请参考[https://github.com/spring-projects/spring-security-oauth/issues/734](https://github.com/spring-projects/spring-security-oauth/issues/734)

访问[http://127.0.0.1:9999/api/demo](http://127.0.0.1:9999/api/demo)，由于SSO的机制，该链接会被重定向到auth server [http://127.0.0.1:8080/login](http://127.0.0.1:8080/login)

### Reference

 * 中文博客：http://www.ruanyifeng.com/blog/2014/05/oauth_2_0.html
 * 英文博客：https://www.digitalocean.com/community/tutorials/an-introduction-to-oauth-2
 * Spring Security OAuth2: http://projects.spring.io/spring-security-oauth/docs/oauth2.html
 * 官方示例：https://spring.io/guides/tutorials/spring-boot-oauth2/