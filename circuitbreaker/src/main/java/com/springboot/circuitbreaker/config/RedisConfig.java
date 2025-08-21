package com.springboot.circuitbreaker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.springboot.circuitbreaker.domain.order.Order;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Order> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Order> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setDefaultSerializer(new Jackson2JsonRedisSerializer<>(Order.class));
        return redisTemplate;
    }
}
