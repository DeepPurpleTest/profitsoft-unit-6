package org.example.profitsoftunit6.controller;

import lombok.RequiredArgsConstructor;
import org.example.profitsoftunit6.auth.dto.UserInfo;
import org.example.profitsoftunit6.model.UserSession;
import org.example.profitsoftunit6.service.SessionService;
import org.example.profitsoftunit6.service.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProfileController {

	private final SessionService sessionService;

	@GetMapping("/profile")
	public Mono<UserInfo> profile(ServerWebExchange exchange) {
		return sessionService.checkSession(exchange)
				.flatMap(this::toUserInfo)
				.onErrorResume(UnauthorizedException.class, e -> {
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access");
				});
	}

	private Mono<UserInfo> toUserInfo(UserSession session) {
		return Mono.just(UserInfo.builder()
				.email(session.getEmail())
				.name(session.getName())
				.authorities(session.getAuthorities())
				.build());
	}
}
