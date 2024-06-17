package org.example.profitsoftunit6gateway.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.profitsoftunit6gateway.auth.dto.UserInfo;
import org.example.profitsoftunit6gateway.model.UserSession;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserSessionRepository {

	private final ReactiveRedisConnectionFactory factory;
	private final ReactiveRedisOperations<String, UserSession> userSessionOps;

//	@PostConstruct
//	public void loadData() {
//		factory.getReactiveConnection().serverCommands().flushAll().thenMany(
//						Flux.just("Jet Black Redis", "Darth Redis", "Black Alert Redis")
//								.map(name -> new UserSession(UUID.randomUUID().toString(), "some.email@gmail.com", name, Instant.now()))
//								.flatMap(session -> userSessionOps.opsForValue().set(session.getId(), session)))
//				.thenMany(userSessionOps.keys("*")
//						.flatMap(userSessionOps.opsForValue()::get))
//				.subscribe(object -> log.info("Object from redis {}", object));
//	}

	public Mono<UserSession> createSession(UserInfo userInfo, Instant expiresAt) {
		UserSession userSession = new UserSession();
		userSession.setId(UUID.randomUUID().toString());
		userSession.setEmail(userInfo.getEmail());
		userSession.setName(userInfo.getName());
		userSession.setExpiresAt(expiresAt);
		return userSessionOps.opsForValue().set(userSession.getId(), userSession)
				.thenReturn(userSession);
	}

	public Mono<UserSession> findById(String id) {
		return userSessionOps.opsForValue().get(id);
	}
}
