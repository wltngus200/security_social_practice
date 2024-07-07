package com.green.practice_security_social.user;

import com.green.practice_security_social.common.model.AppProperties;
import com.green.practice_security_social.common.CookieUtils;
import com.green.practice_security_social.security.MyUser;
import com.green.practice_security_social.security.SignInProviderType;
import com.green.practice_security_social.security.jwt.JwtTokenProvider;
import com.green.practice_security_social.user.model.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtils cookieUtils;
    private final AppProperties appProperties;

    public int postUser(SignUpPostReq p){
        p.setProviderType(SignInProviderType.LOCAL);

        String hash=passwordEncoder.encode(p.getUpw());
        p.setUpw(hash);

        return mapper.postUser(p);
    }

    public SignInRes postSignIn(HttpServletResponse res, SignInPostReq p){
        p.setProviderType(SignInProviderType.LOCAL.name().toUpperCase());
        User user=mapper.getUserId(p);
        if(user==null){throw new RuntimeException("(′д｀σ)σ 너는 누구야?");
        }else if(!passwordEncoder.matches(p.getUpw(),user.getUpw())){
            throw new RuntimeException("(o゜▽゜)o☆ 비밀번호 틀렸쪄");
        }

        MyUser myUser= MyUser.builder().userId(user.getUserId()).role("ROLE_USER").build();

        String accessToken= jwtTokenProvider.generateAccessToken(myUser);
        String refreshToken=jwtTokenProvider.generateRefreshToken(myUser);

        int refreshTokenMaxAge=appProperties.getJwt().getRefreshTokenCookieMaxAge();
        cookieUtils.deleteCookie(res, appProperties.getJwt().getRefreshTokenCookieName());
        cookieUtils.setCookie(res, appProperties.getJwt().getRefreshTokenCookieName(), refreshToken, refreshTokenMaxAge);

        return SignInRes.builder()
                .userId(user.getUserId())
                .nm(user.getNm())
                .pic(user.getPic())
                .accessToken(accessToken)
                .build();
    }

    public Map getAccessToken(HttpServletRequest req){
        Cookie cookie=cookieUtils.getCookie(req, appProperties.getJwt().getRefreshTokenCookieName());

        if(cookie==null){ //refresh 토큰 존재
            throw new RuntimeException();
        }
        String refreshToken/*MyUser를 가지고 있음*/=cookie.getValue();
        if(!jwtTokenProvider.isValidateToken(refreshToken)){ //시간이 만료되지 않음
            throw new RuntimeException();
        }
        Authentication auth=jwtTokenProvider.getAuthentication(refreshToken);

        Map map=new HashMap(); //안 쓰는 걸 추천
        map.put("accessToken", "");
        return map;
    }

    public UserInfoGetRes getUserInfo(UserInfoGetReq p){
        return mapper.selProfileUserInfo(p);
    }

}
