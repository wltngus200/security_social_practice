package com.green.practice_security_social.oauth2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public abstract class OAuth2UserInfo {
    protected final Map<String, Object> attribute;

    public abstract String getId();
    public abstract String getName();
    public abstract String getEmail();
    public abstract String getProfilePicUrl();
}
