package com.green.practice_security_social.user;

import com.green.securitypractice.user.model.*;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    int postUser(SignUpPostReq p);
    SignInRes postSignIn(HttpServletResponse res, SignInPostReq p);
    UserInfoGetRes getUserInfo(UserInfoGetReq p);
}
