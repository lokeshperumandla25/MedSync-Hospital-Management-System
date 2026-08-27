package com.flmhospitals.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
	
	@Bean
	public RouteLocator customLocator(RouteLocatorBuilder builder) {
		
		return builder.routes()
				.route("patientService", r -> r
						.path("/patients/**")
						.uri("lb://PATIENTSERVICE"))
				  .route("appointment-service", r -> r
	                        .path("/appointments/**")
	                        .uri("lb://APPOINTMENTSERVICE"))
	                
	                .route("bed-service", r -> r
	                        .path("/bed/**")
	                        .uri("lb://BEDSERVICE"))
	                
	                .route("room-service", r -> r
	                        .path("/rooms/**")
	                        .uri("lb://BEDSERVICE"))
	                
	                .route("staff-service", r -> r
	                        .path("/staff/**")
	                        .uri("lb://STAFFSERVICE"))
	                
	                .route("doctor-schedule-service", r -> r
	                        .path("/doctorSchedule/**")
	                        .uri("lb://STAFFSERVICE"))
	                
	                .route("notification-service", r -> r
	                        .path("/api/notifications/**")
	                        .uri("lb://NOTIFICATIONSERVICE"))
	                
	                .build();
				
		
	}

}
