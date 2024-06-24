package org.example.profitsoftunit6gateway.service;

import lombok.RequiredArgsConstructor;
import org.example.profitsoftunit6gateway.auth.dto.UserInfo;
import org.example.profitsoftunit6gateway.model.Authorities;
import org.example.profitsoftunit6gateway.model.UserSession;
import org.example.profitsoftunit6gateway.repository.UserSessionRepository;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.example.profitsoftunit6gateway.service.AuthService.COOKIE_SESSION_ID;

@Service
@RequiredArgsConstructor
public class SessionService {

	public static final Duration SESSION_DURATION = Duration.ofMinutes(60);
	public static final Duration SESSION_UPDATE_TIME = Duration.ofMinutes(10);

	private final UserSessionRepository userSessionRepository;

	public Mono<UserSession> checkSession(ServerWebExchange exchange) {
		HttpCookie sessionCookie = exchange.getRequest().getCookies().getFirst(COOKIE_SESSION_ID);
		if (sessionCookie == null) {
			return Mono.error(new UnauthorizedException("Session Cookie not found"));
		}

		return userSessionRepository.findById(sessionCookie.getValue())
				.flatMap(session -> {
							if (session.isExpired()) {
								return Mono.error(new UnauthorizedException("Session expired"));
							}

							if (isExtendTime(session)) {
								return updateSessionTime(session)
										.flatMap(updatedSession -> addSessionCookie(exchange, updatedSession)
												.thenReturn(updatedSession));
							}

							return Mono.just(session);
						}
				).switchIfEmpty(Mono.error(new UnauthorizedException("Session not found")));
	}

	public Mono<UserSession> saveSession(UserInfo userInfo) {
		UserSession userSession = mapToUserSession(userInfo);

		List<Authorities> authorities = new ArrayList<>(
				Arrays.asList(
						Authorities.ENABLE_SEE_PROJECTS_PAGE,
						Authorities.ENABLE_SEE_SECRET_PAGE));
		userSession.setAuthorities(authorities);

		return userSessionRepository.save(userSession, SESSION_DURATION);
	}

	private UserSession mapToUserSession(UserInfo userInfo) {
		UserSession userSession = new UserSession();
		userSession.setId(UUID.randomUUID().toString());
		userSession.setEmail(userInfo.getEmail());
		userSession.setName(userInfo.getName());
		userSession.setExpiresAt(Instant.now().plus(SESSION_DURATION));

		return userSession;
	}

	private boolean isExtendTime(UserSession userSession) {
		Instant now = Instant.now();
		Instant expiresAt = userSession.getExpiresAt();
		Duration timeLeft = Duration.between(now, expiresAt);

		return timeLeft.getSeconds() < SESSION_UPDATE_TIME.getSeconds();
	}
	private Mono<UserSession> updateSessionTime(UserSession userSession) {
		userSession.setExpiresAt(Instant.now().plus(SESSION_DURATION));

		return userSessionRepository.save(userSession, SESSION_DURATION);
	}

	public Mono<Void> addSessionCookie(ServerWebExchange exchange, UserSession session) {
		return Mono.fromRunnable(() -> exchange.getResponse().addCookie(ResponseCookie.from(COOKIE_SESSION_ID)
				.value(session.getId())
				.path("/")
				.maxAge(SESSION_DURATION)
				.secure(true)
				.httpOnly(true) // Prevents JavaScript from accessing the cookie
				.build()));
	}
}
