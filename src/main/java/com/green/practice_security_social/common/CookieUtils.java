package com.green.practice_security_social.common;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class CookieUtils {
    public Cookie getCookie(HttpServletRequest req, String name){
        Cookie[] cookies=req.getCookies();
        if(cookies!=null && cookies.length>0){
            for(Cookie cookie : cookies){
                if(name.equals(cookie.getName())){
                    return cookie;
                }
            }
        }
        return null;
    }

    public <T> T getCookie(HttpServletRequest req, String name, Class<T> valueType){
        Cookie cookie =getCookie(req, name);
        if(cookie==null){return null;}
        if(valueType==String.class){
            return (T) cookie.getValue();
        }
        return deserialize(cookie, valueType);
    }

    public void setCookie(HttpServletResponse res, String name, String value, int maxAge){
        Cookie cookie =new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        res.addCookie(cookie);
    }

    public void setCookie(HttpServletResponse res, String name, Object obj, int maxAge){
       this.setCookie(res, name, serialize(obj), maxAge);
    }


    public void deleteCookie(HttpServletResponse res, String name){
        setCookie(res, name, null, 0);
    }
    public String serialize(Object obj){
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(obj));
    }
    public <T> T deserialize(Cookie cookie, Class<T> cls){
        return cls.cast(
                SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())

                )
        );
    }
}
