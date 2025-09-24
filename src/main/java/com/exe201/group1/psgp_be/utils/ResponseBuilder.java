package com.exe201.group1.psgp_be.utils;

import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseBuilder {
    public static ResponseEntity<ResponseObject> build(HttpStatus status, String message, Object body) {
        return ResponseEntity.status(status)
                .body(
                        ResponseObject.builder()
                                .message(message)
                                .data(body)
                                .build()
                );
    }
}
