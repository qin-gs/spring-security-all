package com.example.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@DisplayName("servlet 集成")
public class WebTest {

    private HttpServletRequest request;
    private HttpServletResponse response;

    @Test
    public void servlet2_5() {
        // 获取当前用户名(可以通过检测该字段判断是否登录)
        String remoteUser = request.getRemoteUser();
        SecurityContextHolder.getContext().getAuthentication().getName();

        // 如果用 UsernamePasswordAuthenticationToken 可以获取用户的一些信息
        Principal principal = request.getUserPrincipal();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 判断用户是否具有某些角色
        boolean inRole = request.isUserInRole("admin");
        boolean admin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains("admin");
    }

    @Test
    public void servlet3_0() throws Exception {
        // 确保用户进行身份验证
        // 如果未通过，使用 AuthenticationEntryPoint 重定向到登录页面
        boolean authenticate = request.authenticate(response);

        // 通过当前 AuthenticationManager 认证用户
        request.login("username", "password");
        // 注销当前用户 会清除 SecurityContextHolder，使 HttpSession 无效，清除'记住我'
        request.logout();

        // 使用 Spring Security 的并发支持，
        // Spring Security 重写 AsyncContext.start(Runnable) 以确保在处理 Runnable 时使用当前的 SecurityContext。
        // 以下将输出当前用户的身份验证
        AsyncContext async = request.startAsync();
        async.start(() -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            try {
                final HttpServletResponse asyncResponse = (HttpServletResponse) async.getResponse();
                asyncResponse.setStatus(HttpServletResponse.SC_OK);
                asyncResponse.getWriter().write(String.valueOf(authentication));
                async.complete();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void servlet3_1() {
        // 防范  session fixation 攻击
        String s = request.changeSessionId();
    }
}
