package com.example.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@DisplayName("jwt 测试")
public class JwtTest {

    @org.junit.jupiter.api.Test
    public void create() {

        // Calendar instance = Calendar.getInstance();
        // instance.add(Calendar.DATE, 2);
        // java.util.Date to = instance.getTime();

        // 获取两天后的当前时间
        java.util.Date to = Date.from(
                LocalDateTime.now()
                        .plus(2, ChronoUnit.DAYS)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );
        String token = JWT.create()
                .withClaim("id", "12")
                .withClaim("username", "admin")
                .withExpiresAt(to)
                .sign(Algorithm.HMAC256("secret".getBytes(StandardCharsets.UTF_8)));
        System.out.println(token);
    }

    @Test
    public void analyse() {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9." +
                "eyJpZCI6IjEyIiwiZXhwIjoxNjM2NDY0NjgwLCJ1c2VybmFtZSI6ImFkbWluIn0." +
                "_gjdSl0rIaD-LKj7jupq_7MHaviCPC-UoxawjuQgico";
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256("secret".getBytes(StandardCharsets.UTF_8))).build();
        DecodedJWT jwt = verifier.verify(token);

        System.out.println(jwt.getClaim("id").asString());
        System.out.println(jwt.getClaim("username").asString());
        System.out.println(jwt.getExpiresAt());

    }
}
