package com.example.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@DisplayName("spring security 测试")
public class SpringSecurityTest {

    @Test
    public void context() {
        // 默认情况下，SecurityContextHolder 使用 ThreadLocal 来存储这些详细信息，提供对 SecurityContext 的访问权限
        // 保存 Authentication 以及可能特定于请求的安全信息
        SecurityContext context = SecurityContextHolder.getContext();
        // 以特定于 Spring Security 的方式表示主体 principal
        Authentication authentication = context.getAuthentication();
        // 获取当前用户信息
        Object principal = authentication.getPrincipal();
        // Spring Security 中的大多数身份验证机制都会返回 UserDetails 的实例作为主体。
        // 提供必要的信息以从应用程序的 DAO 或其他安全数据源构建 Authentication 对象
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
        }
        // UserDetailsService 以便在传入基于String的用户名(或证书 ID 等)时创建 UserDetails

        // 反映授予主体(subject)的应用程序范围的权限，通常是 UserDetailsService 提供的
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    }
}
