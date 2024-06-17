package org.example.profitsoftunit6gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.profitsoftunit6gateway.auth.GoogleAuthenticationService;
import org.example.profitsoftunit6gateway.service.AuthService;
import org.example.profitsoftunit6gateway.service.SessionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OauthController {

	private final AuthService authService;
	private final GoogleAuthenticationService googleAuthenticationService;
	private final SessionService sessionService;

	@GetMapping("/authenticate")
	public Mono<Void> authenticate(ServerWebExchange exchange) {
		log.info("/authenticate");

		String state = UUID.randomUUID().toString();
		authService.addStateCookie(exchange, state);
		String redirectUri = authService.buildRedirectUri(exchange.getRequest());
		String authenticationUrl = googleAuthenticationService.generateAuthenticationUrl(redirectUri, state);
		return authService.sendRedirect(exchange, authenticationUrl);
	}

	@GetMapping("/callback")
	public Mono<Void> authCallback(ServerWebExchange exchange) {
		log.info("/callback");

		String code = exchange.getRequest().getQueryParams().getFirst("code");
		String state = exchange.getRequest().getQueryParams().getFirst("state");
		String redirectUri = authService.buildRedirectUri(exchange.getRequest());
		return authService.verifyState(state, exchange.getRequest())
				.then(googleAuthenticationService.processAuthenticationCallback(code, redirectUri)
						.doOnNext(userInfo -> log.info("User authenticated: {}", userInfo))
						.flatMap(sessionService::saveSession)
						.flatMap(session -> sessionService.addSessionCookie(exchange, session))
						.then(authService.sendRedirect(exchange, "/api/profile")));
	}
}
