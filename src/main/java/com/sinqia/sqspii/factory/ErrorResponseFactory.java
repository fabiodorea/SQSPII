package com.sinqia.sqspii.factory;


import com.sinqia.sqspii.enums.ErrorCode;
import com.sinqia.sqspii.response.ErrorResponse;
import com.sinqia.sqspii.response.SinqiaError;

import java.util.ArrayList;
import java.util.List;

public class ErrorResponseFactory {

    private ErrorResponseFactory() {
        throw new UnsupportedOperationException("Do not instantiate this class, use statically.");
    }

    public static <T> ErrorResponse build(String errorCode, String message, List<SinqiaError> errors) {
        return ErrorResponse.builder()
                .code(errorCode)
                .message(message)
                .body(errors)
                .build();
    }

    public static ErrorResponse build(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.toString())
                .message(errorCode.getMessage())
                .body(new ArrayList<>())
                .build();
    }

    public static ErrorResponse build(ErrorCode errorCode, String message) {
        return ErrorResponse.builder()
                .code(errorCode.toString())
                .message(message)
                .body(new ArrayList<>())
                .build();
    }


}
