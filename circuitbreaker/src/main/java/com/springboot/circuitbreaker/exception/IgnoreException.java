package com.springboot.circuitbreaker.exception;

/**
 * 보통의 경우는 비즈니스 예외를 여기서 무시하도록
 */
public class IgnoreException extends RuntimeException {
    public IgnoreException(String message) {
        super(message);
    }
}
