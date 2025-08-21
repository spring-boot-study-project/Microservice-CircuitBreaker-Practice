package com.springboot.circuitbreaker.domain.predicate;

import java.net.ConnectException;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;

/**
 * 실제 프러덕션에서는 이러한 예외에 대해서 실패 처리가 5xx에 대해서만 실패 처리가 되어야함 4xx는 무시되어야함
 * 지금은 분리되어서 각각을 처리하도록 하고 있는데 하나로 처리해도 될듯..?
 */
public class HttpFailurePredicate implements Predicate<Throwable>{

    @Override
    public boolean test(Throwable throwable) {
        // 서킷이 열려있을 경우는 실패로 처리되면 안된다.
        if (throwable instanceof CallNotPermittedException) {
            return false;
        }

        // 본인이 정의한 BussinessException으로...
        if (throwable instanceof TimeoutException) {
            TimeoutException ex = (TimeoutException) throwable;
            // HttpStatus status = ex.getErrorCode().getStatus(); // 이런식으로...
            HttpStatus status = HttpStatus.REQUEST_TIMEOUT; // 예시로 아무거나 적음

            if (status.is5xxServerError()) {
                return true; // 5로 시작하는 경우 실패로 처리
            }
            if (status.is4xxClientError()) {
                return false; // 4는 서버 자체의 문제가 아님
            }
        }

        if (throwable instanceof TimeoutException || throwable instanceof ConnectException) {
            return true;
        }

        return throwable instanceof DataAccessException;
    }
    
}
