package com.green.practice_security_social.user;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
@Data
@Builder
public class ResultDto<T> {
    private HttpStatus statusCode;
    private String resultMsg;
    private T resultData;
}
