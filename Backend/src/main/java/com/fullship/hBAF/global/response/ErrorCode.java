package com.fullship.hBAF.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /* 예시 필요한 것 추가해서 사용*/
    TEST_NOT_FOUND(HttpStatus.NOT_FOUND, "전달할 메시지"),

    /* TMapApi 요청 실패 */
    NO_AVAILABLE_API(HttpStatus.BAD_REQUEST, "API 요청에 실패하였습니다."),

    /* JSON Parsing 실패 */
    JSON_PARSE_IMPOSSIBLE(HttpStatus.BAD_GATEWAY, "API Parsing 중 오류가 발생했습니다."),

    /* URI */
    URI_SYNTAX_ERROR(HttpStatus.BAD_GATEWAY, "외부 API 호출 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;

    private final String message;
}
