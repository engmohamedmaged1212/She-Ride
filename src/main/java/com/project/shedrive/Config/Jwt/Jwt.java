package com.project.shedrive.Config.Jwt;

import com.project.shedrive.User.User;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;

import javax.crypto.SecretKey;
import java.util.Date;

@AllArgsConstructor
public class Jwt {
    private final Claims claims;
    private final SecretKey secretKey ;
    private final String tokenValue ;
    public boolean isValid(){
        return claims.getExpiration().after(new Date());
    }
    public Long getUserId(){
        return Long.valueOf(claims.getSubject());
    }
    public User.Role getRole(){
        return User.Role.valueOf(claims.get("role" , String.class));
    }

    @Override
    public String toString() {
        return this.tokenValue;
    }
}
