package org.example.profitsoftunit6.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.profitsoftunit6.auth.GoogleAuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

	private static final String PREFIX_OAUTH = "/oauth";

	public static final String COOKIE_AUTH_STATE = "auth-state";

	public static final String COOKIE_SESSION_ID = "SESSION-ID";

	private static final String ENDPOINT_CALLBACK = PREFIX_OAUTH + "/callback";

	private final GoogleAuthenticationService googleAuthenticationService;

	private final SessionService sessionService;

	public Mono<Void> authenticate(ServerWebExchange exchange) {
		String state = UUID.randomUUID().toString();
		addStateCookie(exchange, state);
		String redirectUri = buildRedirectUri(exchange.getRequest());
		String authenticationUrl = googleAuthenticationService.generateAuthenticationUrl(redirectUri, state);

		return sendRedirect(exchange, authenticationUrl);
	}

	public Mono<Void> authCallback(ServerWebExchange exchange) {
		String code = exchange.getRequest().getQueryParams().getFirst("code");
		String state = exchange.getRequest().getQueryParams().getFirst("state");
		String redirectUri = buildRedirectUri(exchange.getRequest());

		return verifyState(state, exchange.getRequest())
				.then(googleAuthenticationService.processAuthenticationCallback(code, redirectUri)
						.doOnNext(userInfo -> log.info("User authenticated: {}", userInfo))
						.flatMap(sessionService::saveSession)
						.flatMap(session -> sessionService.addSessionCookie(exchange, session))
						.then(sendRedirect(exchange, "http://localhost:3050/projects")));
	}

	private void addStateCookie(ServerWebExchange exchange, String state) {
		exchange.getResponse().addCookie(ResponseCookie.from(COOKIE_AUTH_STATE)
				.value(state)
				.path(PREFIX_OAUTH)
				.maxAge(Duration.of(30, ChronoUnit.MINUTES))
				.secure(true)
				.build());
	}

	private String buildRedirectUri(ServerHttpRequest request) {
		String baseUrl = getBaseUrl(request);

		return baseUrl + ENDPOINT_CALLBACK;
	}

	private static String getBaseUrl(ServerHttpRequest request) {
		return request.getURI().toString().substring(0, request.getURI().toString().indexOf(PREFIX_OAUTH));
	}

	private Mono<Void> sendRedirect(ServerWebExchange exchange, String location) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(HttpStatus.FOUND);
		response.getHeaders().add("Location", location);

		return response.setComplete();
	}

	private Mono<Void> verifyState(String state, ServerHttpRequest request) {
		String cookieState = request.getCookies().getFirst(COOKIE_AUTH_STATE).getValue();
		if (!state.equals(cookieState)) {
			return Mono.error(new IllegalStateException("Invalid state"));
		}

		return Mono.empty();
	}
}
