package com.flmhospitals.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.flmhospitals.security.JwtService;

import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class FeignClientConfig {
	
	 private final JwtService jwtService;

	    public FeignClientConfig(JwtService jwtService) {
	        this.jwtService = jwtService;
	    }
	    
	    @Bean
	    Logger.Level feignLoggerLevel() {
	        return Logger.Level.FULL;
	    }

	    @Bean
	    public RequestInterceptor requestInterceptor() {
	        return new RequestInterceptor() {
	            @Override
	            public void apply(RequestTemplate template) {
	                System.out.println("Feign Request Interceptor - URL: " + template.url());
	                System.out.println("Feign Request Interceptor - Method: " + template.method());
	                
	                // Try to get token from current request context first
	                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
	                
	                if (attributes != null) {
	                    HttpServletRequest request = attributes.getRequest();
	                    String authHeader = request.getHeader("Authorization");
	                    
	                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
	                        System.out.println("Feign: Using token from request context");
	                        template.header("Authorization", authHeader);
	                        return;
	                    }
	                }
	                
	                // If no token in request, generate a service token for internal communication
	                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	                
	                if (authentication != null && authentication.isAuthenticated()) {
	                    String subject = authentication.getName();
	                    String role = authentication.getAuthorities().stream()
	                            .findFirst()
	                            .map(auth -> auth.getAuthority().replace("ROLE_", ""))
	                            .orElse("SERVICE");
	                    
	                    System.out.println("Feign: Generating token for authenticated user: " + subject + " with role: " + role);
	                    String token = jwtService.generateToken(subject, role);
	                    template.header("Authorization", "Bearer " + token);
	                } else {
	                    // Generate a service-to-service token for unauthenticated internal calls
	                    System.out.println("Feign: Generating service-to-service token");
	                    String serviceToken = jwtService.generateToken("staff-service", "SERVICE");
	                    template.header("Authorization", "Bearer " + serviceToken);
	                }
	            }

	        };
	    }
	}

