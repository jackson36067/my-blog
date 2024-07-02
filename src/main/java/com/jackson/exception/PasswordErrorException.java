package com.jackson.exception;

public class PasswordErrorException extends BaseException{
    public PasswordErrorException() {
        super();
    }

    public PasswordErrorException(String message) {
        super(message);
    }
}
