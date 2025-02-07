package com.example.security.security;

import org.springframework.security.core.AuthenticationException;

public class InvalidJwtAuthenticationException extends AuthenticationException {
    public InvalidJwtAuthenticationException(String msg) {
        super(msg);
    }

    public InvalidJwtAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}