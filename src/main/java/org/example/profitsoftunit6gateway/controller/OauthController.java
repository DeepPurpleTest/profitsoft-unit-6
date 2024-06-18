package org.example.profitsoftunit6gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.profitsoftunit6gateway.service.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OauthController {

	private final AuthService authService;

	@GetMapping("/authenticate")
	public Mono<Void> authenticate(ServerWebExchange exchange) {
		return authService.authenticate(exchange);
	}

	@GetMapping("/callback")
	public Mono<Void> authCallback(ServerWebExchange exchange) {
		return authService.authCallback(exchange);
	}
}
