package org.example.profitsoftunit6gateway.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.profitsoftunit6gateway.model.UserSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class UserSessionConfiguration {

	@Bean
	ReactiveRedisOperations<String, UserSession> redisOperations(ReactiveRedisConnectionFactory factory) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		Jackson2JsonRedisSerializer<UserSession> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, UserSession.class);

		RedisSerializationContext.RedisSerializationContextBuilder<String, UserSession> builder =
				RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

		RedisSerializationContext<String, UserSession> context = builder.value(serializer).build();

		return new ReactiveRedisTemplate<>(factory, context);
	}
}
