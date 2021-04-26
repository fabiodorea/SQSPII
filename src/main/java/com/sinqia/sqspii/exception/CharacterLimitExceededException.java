package com.sinqia.sqspii.exception;

public class CharacterLimitExceededException extends RuntimeException {

    public CharacterLimitExceededException(String s) {
        super(s);
    }
}
