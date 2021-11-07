package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

/**
 * 开启 Secured 注解使用
 */
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService service;

    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/login.html") // 登录页面设置
                .loginProcessingUrl("/login") // 登录访问路径
                .defaultSuccessUrl("/success.html") // 登录成功后跳转路径
                .permitAll()
                .and().authorizeRequests()
                .antMatchers("/allPage").permitAll()
                .antMatchers("/teacher").hasRole("teacher")
                .antMatchers("/student").hasRole("student")
                .antMatchers("/anyRole").hasAnyRole("teacher", "student") // 满足任意一个权限就可以
                .antMatchers("/read").hasAuthority("read")
                .antMatchers("/write").hasAuthority("write")
                .antMatchers("/anyAuthority").hasAnyAuthority("write", "read")
                .anyRequest().authenticated()
                // 配置 remember me
                .and().rememberMe().tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(60 * 60 * 24 * 7).userDetailsService(service)
                // csrf 跨站请求伪造 默认开启
                .and().csrf().disable();
        // 设置没有权限时的调整页面(403)
        http.exceptionHandling().accessDeniedPage("/unauth.html");
        // 设置退出页面
        http.logout().logoutUrl("/logout").logoutSuccessUrl("/login.html").permitAll();

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(service).passwordEncoder(bCryptPasswordEncoder());
        // 内存中配置用户
        // auth.inMemoryAuthentication()
        //         .withUser("teacher").password(password).roles("teacher")
        //         .and().withUser("student").password(password).roles("student")
        //         .and().withUser("admin").password(password).roles("student", "teacher");
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * remember-me 配置
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl repository = new JdbcTokenRepositoryImpl();
        repository.setDataSource(dataSource);
        // repository.setCreateTableOnStartup(true); // 项目启动时创建表
        return repository;
    }
}
