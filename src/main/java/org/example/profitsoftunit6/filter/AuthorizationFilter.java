package org.example.profitsoftunit6.filter;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.profitsoftunit6.service.SessionService;
import org.example.profitsoftunit6.service.UnauthorizedException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizationFilter implements GlobalFilter, Ordered {

	public static final String PREFIX_API = "/api";
	private final SessionService sessionService;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		log.info("Time: {}", LocalDateTime.now());
		log.info("Cookies: {}", exchange.getRequest().getCookies());
		log.info("URL: {}", exchange.getRequest().getPath().value());

		if (exchange.getRequest().getPath().value().startsWith(PREFIX_API)) {
			return sessionService.checkSession(exchange)
					.then(chain.filter(exchange))
					.onErrorResume(UnauthorizedException.class, e -> sendUnauthorized(exchange));
		}

		return chain.filter(exchange);
	}

	public static Mono<Void> sendUnauthorized(ServerWebExchange exchange) {
		exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
		return exchange.getResponse().setComplete();
	}

	@Override
	public int getOrder() {
		return -5;
	}


}
