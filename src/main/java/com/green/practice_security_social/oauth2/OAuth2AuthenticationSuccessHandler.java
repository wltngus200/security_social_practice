package com.green.practice_security_social.oauth2;

import com.green.practice_security_social.*;
import com.green.practice_security_social.jwt.JwtTokenProviderV2;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final OAuth2AuthenticationRequestBasedOnCookieRepository repository;
    private final AppProperties appProperties;
    private final CookieUtils cookieUtils;
    private final JwtTokenProviderV2 jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);
        log.info("targetUrl: {}", targetUrl);
        if (response.isCommitted()) {
            log.error("onAuthenticationSuccess-응답이 만료됨");
            return;
        }
        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

        @Override
        protected String determineTargetUrl (HttpServletRequest request, HttpServletResponse response, Authentication authentication){
            String redirectUri = cookieUtils.getCookie(request, appProperties.getOauth2().getRedirectUriParamCookieName(), String.class/*타입 지정*/);

            if (redirectUri != null && !hasAuthorizedRedirectUri/*yaml 파일에서 조회*/(redirectUri)) {
                throw new IllegalArgumentException("인증되지 않은 Redirect URI입니다.");
            }

            String targetUrl = redirectUri == null ? getDefaultTargetUrl() : redirectUri;
            MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
            MyUserOAuth2VO myUserOAuth2VO = (MyUserOAuth2VO) myUserDetails.getMyUser();
            MyUser myUser = MyUser.builder()
                    .userId(myUserOAuth2VO.getUserId())
                    .role(myUserOAuth2VO.getRole()).build();

            String accessToken = jwtTokenProvider.generateAccessToken(myUser);
            String refreshToken = jwtTokenProvider.generateRefreshToken(myUser);

            int refreshTokenMaxAge = appProperties.getJwt().getRefreshTokenCookieMaxAge();
            cookieUtils.deleteCookie(response, appProperties.getJwt().getRefreshTokenCookieName());
            cookieUtils.setCookie(response, appProperties.getJwt().getRefreshTokenCookieName(),
                                    refreshToken, refreshTokenMaxAge);

            return UriComponentsBuilder.fromUriString(targetUrl)
                    .queryParam("user_id", myUserOAuth2VO.getUserId())
                    .queryParam("nm", myUserOAuth2VO.getNm()).encode()
                    .queryParam("pic", myUserOAuth2VO.getPic())
                    .queryParam("access_token", accessToken)
                    .build().toUriString();

        }

        private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response){
            super.clearAuthenticationAttributes(request);
            repository.removeAuthorizationRequestCookies(response);
        }

        private boolean hasAuthorizedRedirectUri (String uri){
            URI savedCoolieRedirectUri = URI.create(uri);
            log.info("savedCoolieRedirectUri.getHost():{}", savedCoolieRedirectUri.getHost());
            log.info("savedCoolieRedirectUri.getPort():{}", savedCoolieRedirectUri.getPort());
            log.info("savedCoolieRedirectUri.getPath():{}", savedCoolieRedirectUri.getPath());


            for (String redirectUri: appProperties.getOauth2().getAuthorizedRedirectUris()) {
                URI authorizedUri = URI.create(redirectUri);
                if (savedCoolieRedirectUri.getHost().equalsIgnoreCase(authorizedUri.getHost())
                        && savedCoolieRedirectUri.getPort() == authorizedUri.getPort()) {
                    return true;
                }
            }
        return false;
    }
}
