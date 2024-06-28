package org.example.profitsoftunit6.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.profitsoftunit6.model.UserSession;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserSessionRepository {

	private final ReactiveRedisOperations<String, UserSession> userSessionOps;

	public Mono<UserSession> save(UserSession userSession, Duration ttl) {
		return userSessionOps.opsForValue().set(userSession.getId(), userSession, ttl)
				.thenReturn(userSession);
	}

	public Mono<UserSession> findById(String id) {
		return userSessionOps.opsForValue().get(id);
	}

	public Mono<Void> deleteById(String id) {
		return userSessionOps.delete(id).then();
	}
}
