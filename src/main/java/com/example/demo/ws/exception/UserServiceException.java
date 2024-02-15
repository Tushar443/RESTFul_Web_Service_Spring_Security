package com.example.demo.ws.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;


public class UserServiceException extends RuntimeException {
    public UserServiceException(String message) {
        super(message);
    }
}
