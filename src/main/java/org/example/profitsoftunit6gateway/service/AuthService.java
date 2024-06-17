package org.example.profitsoftunit6gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;


@Service
@RequiredArgsConstructor
public class AuthService {

	private static final String PREFIX_OAUTH = "/oauth";

	public static final String COOKIE_AUTH_STATE = "auth-state";

	private static final String ENDPOINT_CALLBACK = PREFIX_OAUTH + "/callback";


	public void addStateCookie(ServerWebExchange exchange, String state) {
		exchange.getResponse().addCookie(ResponseCookie.from(COOKIE_AUTH_STATE)
				.value(state)
				.path(PREFIX_OAUTH)
				.maxAge(Duration.of(30, ChronoUnit.MINUTES))
				.secure(true)
				.build());
	}

	public String buildRedirectUri(ServerHttpRequest request) {
		String baseUrl = getBaseUrl(request);
		return baseUrl + ENDPOINT_CALLBACK;
	}

	private static String getBaseUrl(ServerHttpRequest request) {
		return request.getURI().toString().substring(0, request.getURI().toString().indexOf(PREFIX_OAUTH));
	}

	public Mono<Void> sendRedirect(ServerWebExchange exchange, String location) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(HttpStatus.FOUND);
		response.getHeaders().add("Location", location);
		return response.setComplete();
	}

	public Mono<Void> verifyState(String state, ServerHttpRequest request) {
		String cookieState = request.getCookies().getFirst(COOKIE_AUTH_STATE).getValue();
		if (!state.equals(cookieState)) {
			return Mono.error(new IllegalStateException("Invalid state"));
		}
		return Mono.empty();
	}
}
