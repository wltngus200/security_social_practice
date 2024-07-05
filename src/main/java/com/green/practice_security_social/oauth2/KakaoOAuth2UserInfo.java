package com.green.practice_security_social.oauth2;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {
    public KakaoOAuth2UserInfo(Map<String, Object> attribute) {
        super(attribute);
    }

    @Override
    public String getId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getName() {
        Map<String, Object> properties =(Map<String, Object>)attribute.get("properties");
        return properties==null?null:properties.get("nickname").toString();
    }

    @Override
    public String getEmail() {
        return attribute.get("account_email").toString();
    }

    @Override
    public String getProfilePicUrl() {
        Map<String, Object> properties =(Map<String, Object>)attribute.get("properties");
        return properties==null? null:properties.get("thumbnail_image").toString();
    }
}
