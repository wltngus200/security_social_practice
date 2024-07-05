package com.green.practice_security_social.user;

import com.green.securitypractice.user.model.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    int postUser(SignUpPostReq p);
    User getUserId(SignInPostReq p);
    UserInfoGetRes selProfileUserInfo(UserInfoGetReq p);

    List<User> selTest(long userId);

}
