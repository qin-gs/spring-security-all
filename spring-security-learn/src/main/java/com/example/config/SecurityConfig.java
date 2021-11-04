package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// @EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 授权
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/teacher/**").hasRole("teacher")
                .antMatchers("/student/**").hasRole("student");

        // 没有权限自动跳到登录页面, 默认地址是/login
        http.formLogin();
        // 开启注销功能，注销成功之后调整到登录页
        http.logout().logoutSuccessUrl("/login");

        http.csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 认证
        // 可以从内存或数据库
        String password = new BCryptPasswordEncoder().encode("123456");
        auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
                .withUser("teacher").password(password).roles("teacher")
                .and().withUser("student").password(password).roles("student")
                .and().withUser("admin").password(password).roles("student", "teacher");
    }
}
