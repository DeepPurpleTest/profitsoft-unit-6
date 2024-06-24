package org.example.profitsoftunit6.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.profitsoftunit6.model.UserSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

	private final ObjectMapper objectMapper;

	@Bean
	public ReactiveRedisOperations<String, UserSession> redisOperations(ReactiveRedisConnectionFactory factory) {
		Jackson2JsonRedisSerializer<UserSession> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, UserSession.class);

		RedisSerializationContext.RedisSerializationContextBuilder<String, UserSession> builder =
				RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

		RedisSerializationContext<String, UserSession> context = builder.value(serializer).build();

		return new ReactiveRedisTemplate<>(factory, context);
	}
}
