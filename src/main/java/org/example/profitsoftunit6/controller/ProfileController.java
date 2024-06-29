package org.example.profitsoftunit6.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.profitsoftunit6.auth.dto.UserInfo;
import org.example.profitsoftunit6.service.SessionService;
import org.example.profitsoftunit6.service.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProfileController {

	private final SessionService sessionService;

	@GetMapping("/profile")
	public Mono<UserInfo> profile(ServerWebExchange exchange) {
		log.info("PROFILE ENDPOINT");
		log.info("TIME: {}", LocalDateTime.now());

		return sessionService.checkSession(exchange)
				.flatMap(session -> sessionService.getUserInfo(exchange, session))
				.onErrorResume(UnauthorizedException.class, e -> {
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access");
				});
	}
}
