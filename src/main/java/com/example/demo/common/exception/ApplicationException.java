package com.example.demo.common.exception;

public class ApplicationException extends RuntimeException{
    public ApplicationException(String message) {
        super(message);
    }
}