package com.hyd.pipes_bakery_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.hyd.pipes_bakery_backend.model.ShoppingCart;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, ShoppingCart> redisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, ShoppingCart> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }
}