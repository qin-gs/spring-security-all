package com.example.web;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.util.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 验证token
 */
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        Map<String, String> info = new HashMap<>(4);
        try {
            DecodedJWT verify = JwtUtils.verify(token);
            info.put("state", "a");
            info.put("msg", "登录成功");
            return true;
        } catch (SignatureVerificationException e) {
            info.put("msg", "签名无效");
        } catch (TokenExpiredException e) {
            info.put("msg", "token过期");
        } catch (AlgorithmMismatchException e) {
            info.put("msg", "算法不匹配");
        } catch (Exception e) {
            e.printStackTrace();
        }
        info.put("state", "b");
        String s = new ObjectMapper().writeValueAsString(info);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().println(s);
        return false;
    }
}
