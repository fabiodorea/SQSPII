package com.sinqia.sqspii.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SinqiaError {

    private String code;
    private String message;
    private String field;
    private String solution;

    public SinqiaError() {

    }

    public SinqiaError(String code, String message, String field, String solution) {
        this.code = code;
        this.message = message;
        this.field = field;
        this.solution = solution;
    }

    
}
