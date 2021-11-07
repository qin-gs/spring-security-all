package com.example.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class JwtUtils {
    private static final String SECRET = "secret";

    /**
     * 生成token
     */
    public static String getToken(Map<String, String> map) {
        JWTCreator.Builder builder = JWT.create();

        // payload
        map.forEach(builder::withClaim);

        // 过期时间两天，获取两天后的当前时间
        java.util.Date to = Date.from(
                LocalDateTime.now()
                        .plus(2, ChronoUnit.DAYS)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );
        builder.withExpiresAt(to);//指定令牌的过期时间
        return builder.sign(Algorithm.HMAC256(SECRET.getBytes(StandardCharsets.UTF_8)));// 签名
    }

    /**
     * 验证token
     */
    public static DecodedJWT verify(String token) {
        // 如果有任何验证异常，此处都会抛出异常
        // 如果没有问题，可以根据返回值获取封装的信息
        return JWT.require(Algorithm.HMAC256(SECRET.getBytes(StandardCharsets.UTF_8))).build().verify(token);
    }

}
