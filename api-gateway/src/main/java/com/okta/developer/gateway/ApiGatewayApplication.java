package com.okta.developer.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.factory.TokenRelayGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiGatewayApplication {

	@Autowired
	private TokenRelayGatewayFilterFactory filterFactory;

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	public RouteLocator routeLocator(RouteLocatorBuilder builder) {
		return builder.routes()
			.route("listing", r -> r.path("/listing/**")
				.filters(f -> f.filter(filterFactory.apply()))
				.uri("lb://listing"))
			.route("theater", r -> r.path("/theater/**")
				.filters(f -> f.filter(filterFactory.apply()))
				.uri("lb://theater"))
			.build();
	}
}
