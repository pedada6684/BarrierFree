package com.fullship.hBAF.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode {
    //기본
    OK(HttpStatus.OK, "OK"),

    //기타
    SUCCESS_CODE(HttpStatus.OK, "Success");


    private final HttpStatus httpStatus;
    private final String message;

}