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

