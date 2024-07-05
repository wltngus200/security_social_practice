package com.green.practice_security_social.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.practice_security_social.AppProperties;
import com.green.practice_security_social.MyUser;
import com.green.practice_security_social.MyUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProviderV2 {
    private final ObjectMapper om;
    private final AppProperties appProperties;
    private final SecretKey secretKey;

    public JwtTokenProviderV2(ObjectMapper om, AppProperties appProperties){
        this.om=om;
        this.appProperties=appProperties;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(appProperties.getJwt().getSecret()));
    }

    public String generateAccessToken(MyUser myUser){
        return generateToken(myUser, appProperties.getJwt().getRefreshTokenExpiry());

    }

    public String generateRefreshToken(MyUser myUser) {
        return generateToken(myUser, appProperties.getJwt().getRefreshTokenExpiry());
    }

    private String generateToken(MyUser myUser, long tokenValidMilliSecond){
        return Jwts.builder()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+tokenValidMilliSecond))
                .claims(createClaims(myUser))
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    private Claims createClaims(MyUser myUser){
        try{
            String json = om.writeValueAsString(myUser);
            return Jwts.claims().add("signedUser", json).build();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Claims getAllClaims(String token){
        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private UserDetails getUserDetailsFromToken(String token){
        try{
            Claims claims=getAllClaims(token);
            String json=(String)claims.get("signedUser");
            MyUser myUser=om.readValue(json, MyUser.class);
            MyUserDetails myUserDetails=new MyUserDetails();
            myUserDetails.setMyUser(myUser);
            return myUserDetails;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = getUserDetailsFromToken(token);
        return userDetails == null
                ? null
                : new UsernamePasswordAuthenticationToken(userDetails,
                null,
                userDetails.getAuthorities());

    }

    public boolean isValidateToken(String token){
        try{
            return !getAllClaims(token).getExpiration().before(new Date());

        }catch(Exception e){
            return false;
        }
    }

    public String resolveToken(HttpServletRequest req){
        String jwt=req.getHeader(appProperties.getJwt().getHeaderSchemaName());
        if (jwt==null){return null;}
        if(!jwt.startsWith(appProperties.getJwt().getTokenType())){
            return null;
        }
        return jwt.substring(appProperties.getJwt().getTokenType().length()).trim();
    }
}
