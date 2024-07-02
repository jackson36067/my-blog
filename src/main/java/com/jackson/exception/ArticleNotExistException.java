package com.jackson.exception;

public class ArticleNotExistException extends BaseException{
    public ArticleNotExistException() {
    }

    public ArticleNotExistException(String message) {
        super(message);
    }
}
