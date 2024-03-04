package com.fullship.hBAF.global.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;
@Data
@Builder
public class ErrorResponseEntity {
    private int statusCode;
    private String statusName;
    private String message;

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(ErrorCode ec) {
        return ResponseEntity
                .status(ec.getHttpStatus())
                .body(ErrorResponseEntity.builder()
                        .statusCode(ec.getHttpStatus().value())
                        .statusName(ec.name())
                        .message(ec.getMessage())
                        .build());
    }
}
