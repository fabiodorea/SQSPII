package com.sinqia.sqspii.exception;

import lombok.Data;

@Data
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = -7261822821103494664L;

    public BusinessException() {
    }
    
    public BusinessException(String e) {
        super(e);
    }
    
    public BusinessException(String e, Throwable c) {
        super(e, c);
    }
}
