package com.green.practice_security_social.oauth2;

import com.green.securitypractice.security.MyUserDetails;
import com.green.securitypractice.security.MyUserOAuth2VO;
import com.green.securitypractice.security.SignInProviderType;
import com.green.securitypractice.user.UserMapper;
import com.green.securitypractice.user.model.SignInPostReq;
import com.green.securitypractice.user.model.SignUpPostReq;
import com.green.securitypractice.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class MyOAuth2UserService extends DefaultOAuth2UserService {
    private final UserMapper mapper;
    private final OAuth2UserInfoFactory oAuth2UserInfoFactory;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{
        try{
            return this.process(userRequest);
        }catch(AuthenticationException e){
            throw e;
        }catch(Exception e){
            throw new InternalAuthenticationServiceException(e.getMessage(), e.getCause());
        }
    }
    private OAuth2User process(OAuth2UserRequest userRequest){
        OAuth2User oAuth2User=super.loadUser(userRequest);
        SignInProviderType signInProviderType
                = SignInProviderType.valueOf(userRequest
                    .getClientRegistration()
                    .getRegistrationId()
                    .toUpperCase());


        OAuth2UserInfo oAuth2UserInfo
                = oAuth2UserInfoFactory.getOAuth2UserInfo(signInProviderType, oAuth2User.getAttributes());
        SignInPostReq signInParam = new SignInPostReq();
        signInParam.setUid(oAuth2UserInfo.getId());
        signInParam.setProviderType(signInProviderType.name());
        User user = mapper.getUserId(signInParam);

        if(user==null){
            SignUpPostReq signUpParam=new SignUpPostReq();
            signUpParam.setProviderType(signInProviderType);
            signUpParam.setUid(oAuth2UserInfo.getId());
            signUpParam.setNm(oAuth2UserInfo.getName());
            signUpParam.setPic(oAuth2UserInfo.getProfilePicUrl());
            int result =mapper.postUser(signUpParam);

            user=new User(signUpParam.getUserId(), signUpParam.getUid(), null,
                    signUpParam.getNm(), signUpParam.getPic(), null, null);
        }

        MyUserOAuth2VO myUserOauth2VO
                =new MyUserOAuth2VO(user.getUserId(), "ROLE_USER", user.getNm(), user.getPic());

        MyUserDetails signInUser=new MyUserDetails();
        signInUser.setMyUser(myUserOauth2VO);

        return signInUser;
    }
}
