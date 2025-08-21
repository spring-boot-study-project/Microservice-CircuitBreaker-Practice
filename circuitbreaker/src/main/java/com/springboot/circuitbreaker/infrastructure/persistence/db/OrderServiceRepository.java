package com.springboot.circuitbreaker.infrastructure.persistence.db;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.springboot.circuitbreaker.domain.enums.ProtectionTarget;
import com.springboot.circuitbreaker.domain.order.Order;
import com.springboot.circuitbreaker.domain.order.OrderRepository;
import com.springboot.circuitbreaker.infrastructure.circuitbreaker.aspect.CircuitProtection;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceRepository {
    private final OrderRepository orderRepository;

    @Retry(name = ProtectionTarget.Constants.DB_RETRY) // 재시도가 먼저 실행
    @CircuitProtection(ProtectionTarget.DB)
    public Optional<Order> findById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Retry(name = ProtectionTarget.Constants.DB_RETRY) // 재시도가 먼저 실행
    @CircuitProtection(ProtectionTarget.DB) // 재시도가 다 실패하고 실패율이 내가 설정한 값 이상이 되면 서킷이 실행됨 위의 실패를 최소한으로 줄이기 위해서 재시도를 추가하는 게
                                            // 좋음
    public void save(Order order) {
        orderRepository.save(order);
    }
}
