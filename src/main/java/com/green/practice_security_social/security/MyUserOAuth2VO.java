package com.green.practice_security_social.security;


import lombok.Getter;


@Getter
public class MyUserOAuth2VO extends MyUser{
    private final String nm;
    private final String pic;

    public MyUserOAuth2VO(long userId, String role, String nm, String pic) {
        super(userId, role);
        this.nm = nm;
        this.pic = pic;
    }
}
