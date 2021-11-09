package com.example.config;


import com.example.web.AuthenticationProviderImpl;
import com.example.web.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class Config {

    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    private AuthenticationManager manager;

    /**
     * 需要注册到 AuthenticationManagerBuilder
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new AuthenticationProviderImpl();
    }

    /**
     * 认证成功后
     * <p>
     * SimpleUrlAuthenticationSuccessHandler
     * SavedRequestAwareAuthenticationSuccessHandler
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        // 登录后跳转到登录前页面
        // RequestCache 封装了存储和检索 HttpServletRequest 实例所需的功能。
        // 默认情况下使用 HttpSessionRequestCache，它将请求存储在 HttpSession 中。
        // RequestCacheFilter 的工作是当用户重定向到原始 URL 时从缓存中实际恢复已保存的请求。
        return new SavedRequestAwareAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                User userDetails = (User) authentication.getPrincipal();
                logger.info("USER : " + userDetails.getUsername() + " LOGIN SUCCESS !  ");
                super.onAuthenticationSuccess(request, response, authentication);
            }
        };
    }

    /**
     * 认证失败后
     * <p>
     * SimpleUrlAuthenticationFailureHandler
     * ExceptionMappingAuthenticationFailureHandler
     * DelegatingAuthenticationFailureHandler
     */
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler();
    }

    /**
     * 登出处理
     */
    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new LogoutSuccessHandler() {
            @Override
            public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                try {
                    User user = (User) authentication.getPrincipal();
                    logger.info("USER : " + user.getUsername() + " LOGOUT SUCCESS !  ");
                } catch (Exception e) {
                    logger.info("LOGOUT EXCEPTION , e : " + e.getMessage());
                }
                httpServletResponse.sendRedirect("/login.html");
            }
        };
    }

    /**
     * ExceptionTranslationFilter 位于安全过滤器堆栈中的 FilterSecurityInterceptor 上方。
     * 它本身并没有执行任何实际的安全性强制措施，而是处理安全性拦截器引发的异常并提供适当的 HTTP 响应
     * <p>
     * ExceptionTranslationFilter 职责的另一个职责是在调用 AuthenticationEntryPoint 之前保存当前请求，这允许在用户认证之后恢复请求。
     */
    @Bean
    public ExceptionTranslationFilter exceptionTranslationFilter() {
        // 如果用户请求安全的 HTTP 资源但未通过身份验证，则将调用AuthenticationEntryPoint
        // 安全拦截器将在调用堆栈的更下方抛出适当的 AuthenticationException 或 AccessDeniedException
        ExceptionTranslationFilter filter = new ExceptionTranslationFilter(new LoginUrlAuthenticationEntryPoint("/login.html"));
        filter.setAccessDeniedHandler(accessDeniedHandler());
        return filter;
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        // 如果用户已通过身份验证，但是抛出AccessDeniedException，则意味着已尝试操作该用户没有足够权限的操作
        // 默认情况下，使用AccessDeniedHandlerImpl，它仅向 Client 端发送 403(禁止访问)响应。
        // 或者，可以显式配置实例(如上例所示)，并设置错误页面 URL，它将请求转发至。
        // 这可以是简单的“访问被拒绝”页面，例如 JSP，也可以是更复杂的处理程序，例如 MVC 控制器
        AccessDeniedHandlerImpl deniedHandler = new AccessDeniedHandlerImpl();
        deniedHandler.setErrorPage("/page_403");
        return deniedHandler;
    }

    /**
     * 它负责在 HTTP 请求之间存储 SecurityContext 内容，并在请求完成时清除 SecurityContextHolder
     */
    @Bean
    public SecurityContextPersistenceFilter securityContextPersistenceFilter() {
        // 从 Spring Security 3.0 开始，将安全性上下文的加载和存储工作委托给一个单独的策略接口
        HttpSessionSecurityContextRepository repository = new HttpSessionSecurityContextRepository();
        // 允许该类在需要一个会话来存储经过身份验证的用户的安全上下文时创建会话(除非进行了身份验证，否则它不会创建一个会话)
        repository.setAllowSessionCreation(true);
        return new SecurityContextPersistenceFilter(repository);
    }

    /**
     * 身份验证
     * <p>
     * 如果身份验证成功，则将结果 Authentication 对象放入 SecurityContextHolder。
     * 然后将调用已配置的 AuthenticationSuccessHandler 来将用户重定向或转发到适当的目的地。
     * 默认情况下，使用 SavedRequestAwareAuthenticationSuccessHandler，
     * 这意味着用户将被重定向到他们要求的原始目的地，然后才被要求登录
     */
    @Bean
    public UsernamePasswordAuthenticationFilter authenticationFilter() {
        // 需要配置 AuthenticationManager
        UsernamePasswordAuthenticationFilter filter = new UsernamePasswordAuthenticationFilter();
        filter.setAuthenticationManager(manager);
        return filter;
    }

    /**
     * 负责处理 HTTPHeaders 中显示的基本身份验证凭据
     * 配置的 AuthenticationManager 处理每个身份验证请求。如果身份验证失败，将使用配置的 AuthenticationEntryPoint 重试身份验证过程
     * 与 BasicAuthenticationEntryPoint 结合使用，BasicAuthenticationEntryPoint 返回带有适当 Headers 的 401 响应以重试 HTTP Basic 身份验证。
     * 如果身份验证成功，则照常将生成的 Authentication 对象放入 SecurityContextHolder 中
     * <p>
     * 如果身份验证事件成功，或者因为 HTTPHeaders 不包含受支持的身份验证请求而未尝试进行身份验证，则过滤器链将照常 continue。
     * 唯一的中断过滤器链的方法是验证失败并调用AuthenticationEntryPoint
     */
    @Bean
    public BasicAuthenticationFilter basicAuthenticationFilter() {
        return new BasicAuthenticationFilter(manager, authenticationEntryPoint());
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
        entryPoint.setRealmName("name of realm");
        return entryPoint;
    }

}
