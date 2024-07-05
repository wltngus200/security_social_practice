package com.green.practice_security_social;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@ConfigurationProperties(prefix = "app") //yaml파일의 값 //Application에 어노테이션 추가 필요
public class AppProperties {
    private final Jwt jwt=new Jwt();
    private final Oauth2 oauth2=new Oauth2();

    @Getter
    @Setter
    public static class Jwt{
        private String secret;
        private String headerSchemaName;
        private String tokenType;
        private long accessTokenExpiry;
        private long refreshTokenExpiry;
        private int refreshTokenCookieMaxAge;
        private String refreshTokenCookieName;

        public void setRefreshTokenExpiry(long refreshTokenExpiry){
            this.refreshTokenExpiry=refreshTokenExpiry;
            this.refreshTokenCookieMaxAge=(int)(refreshTokenExpiry*0.001);
        }
    }
    @Getter
    @Setter
    public static class Oauth2{
        private String authorizationRequestCookieName;
        private String redirectUriParamCookieName;
        private int cookieExpirySeconds;
        private List<String> authorizedRedirectUris;
    }

}
