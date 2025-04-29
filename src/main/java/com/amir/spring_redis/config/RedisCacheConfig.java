package com.amir.spring_redis.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import com.amir.spring_redis.dto.ProductDto;

@Configuration
public class RedisCacheConfig {

    @Bean  
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {  
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()  
                .entryTtl(Duration.ofMinutes(10))  
                .disableCachingNullValues()  
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(  
                        new Jackson2JsonRedisSerializer<>(ProductDto.class)));  
  
        return RedisCacheManager.builder(redisConnectionFactory)  
                .cacheDefaults(cacheConfiguration)  
                .build();  
    }  
}