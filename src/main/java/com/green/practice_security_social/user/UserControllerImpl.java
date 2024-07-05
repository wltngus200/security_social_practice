package com.green.practice_security_social.user;

import com.green.securitypractice.user.model.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserControllerImpl {
    private final UserServiceImpl service;

    @PostMapping("sign-up")
    public ResultDto<Integer> postUser(@RequestPart SignUpPostReq p){
        int result=service.postUser( p);
        return ResultDto.<Integer>builder()
                .statusCode(HttpStatus.OK)
                .resultData(result)
                .resultMsg("가입 ╰(*°▽°*)╯ 완료")
                .build();
    }
    @PostMapping("sign-in")
    public ResultDto<SignInRes> postSignIn(HttpServletResponse res, @RequestBody SignInPostReq p){
        SignInRes result=service.postSignIn(res, p);
        return ResultDto.<SignInRes>builder()
                .statusCode(HttpStatus.OK)
                .resultMsg("♪(´▽｀) 어서 와")
                .resultData(result)
                .build();
    }

    @GetMapping("access-token")
    public ResultDto<Map> getRefreshToken(HttpServletRequest req/*요청만 있으면 됨(이유: 프론트에서 get요청 URL만 넘겨도 필요한 정보-refreshToken-이 넘어옴(cookie 사용))*/){
                                                                                          //refreshToken을 로그인을 성공하면 cookie에 담았기 때문 cookie는 요청마다 넘어옴
        Map map=service.getAccessToken(req);
        return ResultDto.<Map>builder()
                .statusCode(HttpStatus.OK)
                .resultMsg("Access Token 발급")
                .resultData(map)
                .build();
    }


    @GetMapping
    public ResultDto<UserInfoGetRes> getUserInfo(@ParameterObject @ModelAttribute UserInfoGetReq p){
        UserInfoGetRes result=service.getUserInfo(p);
        return ResultDto.<UserInfoGetRes>builder()
                .statusCode(HttpStatus.OK)
                .resultMsg("( ⓛ ω ⓛ *) 파칭")
                .resultData(result)
                .build();
    }

}
