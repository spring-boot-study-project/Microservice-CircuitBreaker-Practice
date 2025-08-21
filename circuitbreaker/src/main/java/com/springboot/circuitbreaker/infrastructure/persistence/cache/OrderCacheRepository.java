package com.springboot.circuitbreaker.infrastructure.persistence.cache;

import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.springboot.circuitbreaker.domain.enums.ProtectionTarget;
import com.springboot.circuitbreaker.domain.order.Order;
import com.springboot.circuitbreaker.infrastructure.circuitbreaker.aspect.CircuitProtection;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderCacheRepository {
    private final RedisTemplate<String, Order> redisTemplate;

    @Retry(name = ProtectionTarget.Constants.REDIS_RETRY)
    @CircuitProtection(ProtectionTarget.REDIS)
    public Optional<Order> get(String key) {
        return Optional.of(redisTemplate.opsForValue().get(key));
    }

    @Retry(name = ProtectionTarget.Constants.REDIS_RETRY)
    @CircuitProtection(ProtectionTarget.REDIS)
    public void saveAndCache(String key, Order order) {
        redisTemplate.opsForValue().set(key, order);
    }
}
