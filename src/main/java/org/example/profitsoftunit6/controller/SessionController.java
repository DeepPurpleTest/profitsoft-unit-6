package org.example.profitsoftunit6.controller;

import lombok.RequiredArgsConstructor;
import org.example.profitsoftunit6.service.SessionService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SessionController {

	private final SessionService sessionService;

	@DeleteMapping("/logout")
	public Mono<Void> logout(ServerWebExchange exchange) {
		return sessionService.checkSession(exchange)
				.flatMap(session -> sessionService.invalidateSession(session.getId()));
	}
}
