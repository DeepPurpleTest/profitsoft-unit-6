package org.example.profitsoftunit6.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class GatewayConfig {

	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("service-profitsoft-unit-2",
						r -> r
								.path("/api/**")
								.filters(f -> f
										.rewritePath("/api/(?<remaining>.*)", "/api/${remaining}"))
								.uri("lb://profitsoft-unit-2"))
				.build();
	}
}
