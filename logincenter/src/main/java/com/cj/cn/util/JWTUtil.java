package com.cj.cn.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cj.cn.pojo.User;

import java.util.Date;

public class JWTUtil {
    private final static String secretKey = "23223232"; //私钥
    private final static long expireTime = 1000 * 60 * 30;  //设置token的过期时间为30分钟

    public static String getToken(User user) {
        JWTCreator.Builder builder = JWT.create();
        assert user != null;
        builder.withClaim("id", user.getId());
        builder.withClaim("username", user.getUsername());
        builder.withClaim("email", user.getEmail());
        builder.withClaim("phone", user.getPhone());
        builder.withClaim("role", user.getRole());

        long nowTimeMillis = System.currentTimeMillis();
        if (expireTime > 0) {
            nowTimeMillis += expireTime;
        }
        String token = builder
                .withExpiresAt(new Date(nowTimeMillis))
                .sign(Algorithm.HMAC256(secretKey));
        return token;
    }

    public static void verify(String token) {
        JWT.require(Algorithm.HMAC256(secretKey)).build().verify(token);
    }

    public static String getPayload(String token) {
        DecodedJWT verify = JWT.require(Algorithm.HMAC256(secretKey)).build().verify(token);
        assert verify != null;
        String payload = verify.getPayload();
        return payload;
    }
}
