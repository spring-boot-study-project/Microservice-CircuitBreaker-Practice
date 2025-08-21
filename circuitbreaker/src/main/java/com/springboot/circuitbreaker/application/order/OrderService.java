package com.springboot.circuitbreaker.application.order;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.springboot.circuitbreaker.domain.order.Order;
import com.springboot.circuitbreaker.infrastructure.persistence.cache.OrderCacheRepository;
import com.springboot.circuitbreaker.infrastructure.persistence.db.OrderServiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderServiceRepository dbRepository;
    private final OrderCacheRepository cacheRepository;

    public Order getOrder(Long orderId) {
        // 1. Cache 조회 (Redis 서킷/재시도 보호)
        String key = orderId.toString() + "_order";
        Optional<Order> cachedOrder = cacheRepository.get(key);
        if (cachedOrder.isPresent()) {
            return cachedOrder.get();
        }

        // 2. DB 조회 (DB 서킷/재시도 보호, Fallback으로 Cache 조회)
        return dbRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("조회 안댐"));
    }
}
