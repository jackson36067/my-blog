package com.jackson.exception;

public class UserExistException extends BaseException{
    public UserExistException() {
        super();
    }

    public UserExistException(String message) {
        super(message);
    }
}
