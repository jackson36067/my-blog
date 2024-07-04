package com.jackson.utils;

import com.jackson.constant.JwtConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtils {


    /**
     * 生成jwt令牌
     *
     * @param claims
     * @return
     */
    public static String genJwt(Map<String, Object> claims) {
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, JwtConstant.SIGNATURE_KEY.getBytes(StandardCharsets.UTF_8))
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + JwtConstant.EXPIRE_TIME))
                .compact();
    }

    /**
     * 解析jwt令牌
     * @param token
     * @return
     */
    public static Claims parseJwt(String token) {
        return Jwts.parser()
                .setSigningKey(JwtConstant.SIGNATURE_KEY.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();
    }
}
