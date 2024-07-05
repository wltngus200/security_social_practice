package com.green.practice_security_social;

import com.green.practice_security_social.jwt.JwtAuthenticationAccessDeniedHandler;
import com.green.practice_security_social.jwt.JwtAuthenticationEntryPoint;
import com.green.practice_security_social.jwt.JwtAuthenticationFilter;
import com.green.practice_security_social.oauth2.MyOAuth2UserService;
import com.green.practice_security_social.oauth2.OAuth2AuthenticationFailureHandler;
import com.green.practice_security_social.oauth2.OAuth2AuthenticationRequestBasedOnCookieRepository;
import com.green.practice_security_social.oauth2.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final OAuth2AuthenticationRequestBasedOnCookieRepository repository;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final MyOAuth2UserService myOAuth2UserService;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{


        return httpSecurity.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(http->http.disable())
                .formLogin(form->form.disable())
                .csrf(csrf->csrf.disable())
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(
                                        "/api/feed"
                                        , "/api/feed/*"
                                        , "/api/user/pic"
                                        , "/api/user/follow"
                                )
                                .authenticated()
                                .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                        .accessDeniedHandler(new JwtAuthenticationAccessDeniedHandler())
                )
                .oauth2Login( oauth2 -> oauth2.authorizationEndpoint(
                                        auth -> auth.baseUri("/oauth2/authorization")
                                                .authorizationRequestRepository(repository)

                                )
                                .redirectionEndpoint( redirection -> redirection.baseUri("/*/oauth2/code/*"))
                                .userInfoEndpoint(userInfo -> userInfo.userService(myOAuth2UserService))
                                .successHandler(oAuth2AuthenticationSuccessHandler)
                                .failureHandler(oAuth2AuthenticationFailureHandler)
                ).build();


    }

    @Bean
    public PasswordEncoder passwordEncoder(){ //암호화 된 패스워드 인코딩 할 때 사용 //스프링에서 지정한 표준(어느 인코더를 사용하더라도 가능 ex. 멀티플러그)
        return new BCryptPasswordEncoder();
    }

}

