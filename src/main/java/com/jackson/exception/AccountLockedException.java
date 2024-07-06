package com.jackson.exception;

public class AccountLockedException extends BaseException{
    public AccountLockedException() {
        super();
    }

    public AccountLockedException(String message) {
        super(message);
    }
}
