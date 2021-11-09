#### Spring Security

过滤器链：

FilterSecurityInterceptor：方法级别的权限过滤器

ExceptionTranslationFilter：处理认证授权过程中出现的异常

UsernamePasswordAuthenticationFilter：拦截登录请求，校验用户名密码

...



1. DelegatingFilterProxy

   doFilter -> initDelegate 从 WebApplicationContext 中拿到一个过滤器 FilterChainProxy

2. FilterChainProxy 

   doFilter -> doFilterInternal 从SecurityFilterChain中将所有过滤器加载到过滤器链中 



1. UserDetailsService

   继承UserDetailsService自定义数据库查询过程(从数据库中查询用户名密码)，返回User对象

   继承UsernamePasswordAuthenticationFilter 用来接收用户名密码，重写认证成功或失败后跳转逻辑

2. PasswordEncoder

   数据加密接口



认证 (authentication)

1. 配置文件中写死

   ```yaml
   spring:
     security:
       user:
         name: admin
         password: 123456
   ```

2. 配置类中写死

   ```java
   auth.inMemoryAuthentication()
           .withUser("teacher").password(password).roles("teacher")
   ```

3. 数据库查询

   ```java
   return new User("admin", "123456", authorities)
   ```



RememberMe

首次登录时在 `UsernamePasswordAuthenticationFilter` 中拿到用户名密码进行认证，成功之后它的父类中 `AbstractAuthenticationProcessingFilter#successfulAuthentication` 方法调用 `AbstractRememberMeServices#loginSuccess` 方法使用 `PersistentTokenRepository` 生成 `PersistentRememberMeToken` 放到 cookie 中，同时使用 `JdbcTokenRepositoryImpl` 将其存到数据库中

再次登录时 `RememberMeAuthenticationFilter` 会调用 `AbstractRememberMeServices#autoLogin` 从 cookie 中取出值和数据库中的值进行判断



ExceptionTranslationFilter：负责检测 spring security (通常由 AbstractSecurityInterceptor )引发的异常

AuthenticationEntryPoint：身份认证策略

SecurityContextPersistenceFilter：在请求之间存储 SecurityContext

AccessDecisionManager：做出访问控制决策

AbstractSecurityInterceptor：提供一致的工作流处理安全的对象请求

AfterInvocationManager

ConfigAttribute

RunAsManager



DelegatingFilterProxy

FilterChainProxy

过滤器顺序：

- `ChannelProcessingFilter`，因为它可能需要重定向到其他协议
- `SecurityContextPersistenceFilter`，因此可以在 Web 请求开始时在`SecurityContextHolder`中设置`SecurityContext`，并且在 Web 请求结束时(对下一个 Web 请求可用)对`SecurityContext`所做的任何更改都可以复制到`HttpSession`中。
- `ConcurrentSessionFilter`，因为它使用`SecurityContextHolder`功能并且需要更新`SessionRegistry`以反映来自委托人的持续请求
- 认证处理机制`UsernamePasswordAuthenticationFilter`，`CasAuthenticationFilter`，`BasicAuthenticationFilter`等-以便可以将`SecurityContextHolder`修改为包含有效的`Authentication`请求令牌
- `SecurityContextHolderAwareRequestFilter`，如果您使用它来将支持 Spring Security 的`HttpServletRequestWrapper`安装到 servlet 容器中
- `JaasApiIntegrationFilter`，如果`SecurityContextHolder`中有`JaasAuthenticationToken`，则它将`FilterChain`作为`JaasAuthenticationToken`中的`Subject`处理。
- `RememberMeAuthenticationFilter`，因此，如果没有较早的身份验证处理机制更新`SecurityContextHolder`，并且请求提出一个 cookie 来启用“记住我”服务，则会在此处放置一个合适的记住`Authentication`对象
- `AnonymousAuthenticationFilter`，因此，如果没有较早的身份验证处理机制更新`SecurityContextHolder`，则将在其中放置一个匿名`Authentication`对象
- `ExceptionTranslationFilter`，以捕获任何 Spring Security 异常，以便可以返回 HTTP 错误响应或启动适当的`AuthenticationEntryPoint`
- `FilterSecurityInterceptor`，以保护 Web URI 并在拒绝访问时引发异常



核心安全过滤器

FilterSecurityInterceptor







#### 授权 Authentication

Aythentication 存储在 GrantAuthority 中，代表已授予用户权限

通过 AuthenticationManager 插入到 Authentication 中，做出决策是由 AccessDecisionManager 读取

