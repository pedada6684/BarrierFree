package com.fullship.hBAF.global.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
@Builder
public class CommonResponseEntity {
    private int statusCode;
    private String statusName;
    private String message;
    private Object data;

    public static ResponseEntity<CommonResponseEntity> getResponseEntity(SuccessCode sc, Object data) {
        return ResponseEntity
                .status(sc.getHttpStatus())
                .body(CommonResponseEntity.builder()
                        .statusCode(sc.getHttpStatus().value())
                        .statusName(sc.name())
                        .message(sc.getMessage())
                        .data(data)
                        .build());
    }

    public static ResponseEntity<CommonResponseEntity> getResponseEntity(SuccessCode sc) {
        return ResponseEntity
                .status(sc.getHttpStatus())
                .body(CommonResponseEntity.builder()
                        .statusCode(sc.getHttpStatus().value())
                        .statusName(sc.name())
                        .message(sc.getMessage())
                        .build());
    }
}