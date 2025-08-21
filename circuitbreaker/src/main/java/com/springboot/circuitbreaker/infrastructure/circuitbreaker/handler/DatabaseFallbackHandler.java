package com.springboot.circuitbreaker.infrastructure.circuitbreaker.handler;

import org.springframework.stereotype.Component;

import com.springboot.circuitbreaker.domain.enums.ProtectionTarget;
import com.springboot.circuitbreaker.infrastructure.persistence.cache.OrderCacheRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseFallbackHandler implements FallbackHandler {
    private final OrderCacheRepository orderCacheRepository;

    @Override
    public ProtectionTarget getTarget() {
        return ProtectionTarget.DB;
    }

    @Override
    public Object handle(Throwable throwable, Object... args) {
        Long orderId = (Long) args[0];
        log.warn("DB Fallback 실행! Cache에서 조회를 시도합니다. 원인: {}, orderId: {}", throwable.getMessage(), orderId);
        String key = orderId.toString() + "_order";
        return orderCacheRepository.get(key);
    }
}
