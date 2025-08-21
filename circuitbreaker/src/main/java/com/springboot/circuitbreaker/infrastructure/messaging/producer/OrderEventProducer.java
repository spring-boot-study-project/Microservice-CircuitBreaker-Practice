package com.springboot.circuitbreaker.infrastructure.messaging.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.springboot.circuitbreaker.domain.enums.ProtectionTarget;
import com.springboot.circuitbreaker.domain.order.Order;
import com.springboot.circuitbreaker.infrastructure.circuitbreaker.aspect.CircuitProtection;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // 주문 처리 실패 시 DLQ로 발행
    @Retry(name = "kafka-producer-retry")
    @CircuitProtection(ProtectionTarget.KAFKA_PRODUCER)
    public void sendOrderToDlq(Order order) {
        log.warn("주문 처리 실패. DLQ로 메시지 발행 시도. orderId: {}", order.getOrderId());
        kafkaTemplate.send("order-dlq-topic", order);
        log.warn("DLQ로 메시지 발행 성공. orderId: {}", order.getOrderId());
    }
}
