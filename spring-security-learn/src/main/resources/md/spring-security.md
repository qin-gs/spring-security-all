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



RemenberMe

首次登录时在 `UsernamePasswordAuthenticationFilter` 中拿到用户名密码进行认证，成功之后它的父类中 `AbstractAuthenticationProcessingFilter#successfulAuthentication` 方法调用 `AbstractRememberMeServices#loginSuccess` 方法使用 `PersistentTokenRepository` 生成 `PersistentRememberMeToken` 放到 cookie 中，同时使用 `JdbcTokenRepositoryImpl` 将其存到数据库中

再次登录时 `RememberMeAuthenticationFilter` 会调用 `AbstractRememberMeServices#autoLogin` 从 cookie 中取出值和数据库中的值进行判断



ExceptionTranslationFilter：负责检测 spring security (通常由 AbstractSecurityIterceptor )引发的异常

AuthenticationEntryPoint：身份认证策略

SecurityContextPersistenceFilter：在请求之间存储 SecurityContext

AccessDecisionManager：做出访问控制决策

AbstractSecurityInterceptor：提供一致的工作流处理安全的对象请求

AfterInvocationManager

ConfigAttribute

RunAsManager



