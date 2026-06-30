package com.project.shedrive.Config.Jwt;

import com.project.shedrive.User.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@AllArgsConstructor
@Service
public class JwtServices {
    private final JwtConfig jwtConfig;
    public Jwt generateAccessTokens(User user){
        return generateToken(user, jwtConfig.getAccessTokenExpiration());
    }

    public Jwt generateRefreshTokens(User user){
        return generateToken(user, jwtConfig.getRefreshTokenExpiration());
    }

    private Jwt generateToken(User user, long tokenExpiration) {
        var claims = Jwts.claims()
                .subject(user.getId().toString())
                .add("phone_number" , user.getPhoneNumber())
                .add("role" , user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000L * tokenExpiration))
                .build();
        String tokenValue = Jwts.builder()
                .claims(claims)
                .signWith(jwtConfig.getSecretKey())
                .compact();

        return new Jwt(claims , jwtConfig.getSecretKey() , tokenValue);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Jwt parseToken(String token){
        try{
            var claims =getClaims(token);
            return new Jwt(claims , jwtConfig.getSecretKey() , token);
        } catch (JwtException e) {
            return null ;
        }
    }
}
