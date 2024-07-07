package com.green.practice_security_social.security.oauth2;

import java.util.Map;
public class GoogleOAuth2UserInfo extends OAuth2UserInfo{
    public GoogleOAuth2UserInfo(Map<String, Object> attribute) {
        super(attribute);
    }

    @Override
    public String getId() {
        return (String)attribute.get("sub");
    }

    @Override
    public String getName() {
        return (String)attribute.get("name");
    }

    @Override
    public String getEmail() {
        return (String)attribute.get("email");
    }

    @Override
    public String getProfilePicUrl() {
        return (String)attribute.get("picture");
    }
}
