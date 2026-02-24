package org.project.backend_kotlin.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import tools.jackson.databind.ObjectMapper


@Configuration
class RedisConfig {

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory?): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = connectionFactory

        val keySerializer = StringRedisSerializer()
        val valueSerializer = GenericJacksonJsonRedisSerializer(ObjectMapper())

        template.keySerializer = keySerializer
        template.hashKeySerializer = keySerializer

        template.valueSerializer = valueSerializer
        template.hashValueSerializer = valueSerializer

        template.afterPropertiesSet()
        return template
    }
}