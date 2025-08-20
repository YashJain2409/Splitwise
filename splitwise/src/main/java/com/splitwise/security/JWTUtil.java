package com.splitwise.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JWTUtil {

    private static final  String stringKey = "my-private-key-dkskdnsaknksnfsnfksnfksknskc";
    Key key = Keys.hmacShaKeyFor(stringKey.getBytes(StandardCharsets.UTF_8));
    public String generateToken(String username,int expiryMinute){
        return Jwts.builder().setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (long) expiryMinute * 1000*60))
                .signWith(key,SignatureAlgorithm.HS256)
                .compact();
    }

    public String validateJwtToken(String token){

        try{
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
        }
        catch (Exception e){
            throw e;
        }

    }



}
