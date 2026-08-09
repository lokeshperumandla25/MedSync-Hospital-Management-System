package com.flmhospitals.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private JwtService jwtService;
	
	public JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, 
									HttpServletResponse response, 
									FilterChain filterChain) throws ServletException, IOException {

		 String authHeader = request.getHeader("Authorization");

	        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
	            filterChain.doFilter(request, response);
	            return;
	        }

	        String token = authHeader.substring(7);

	        if (jwtService.isTokenValid(token)) {
	            String subject = jwtService.extractSubject(token);
	            String role = jwtService.extractRole(token);

	            System.out.println("JWT Filter - Subject: " + subject);
	            System.out.println("JWT Filter - Extracted Role: " + role);

	            if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	                // Ensure role is uppercase for Spring Security
	                String normalizedRole = (role != null) ? role.toUpperCase() : null;
	                SimpleGrantedAuthority authority =
	                        normalizedRole != null ? new SimpleGrantedAuthority("ROLE_" + normalizedRole) : null;

	                System.out.println("JWT Filter - Normalized Role: " + normalizedRole);
	                System.out.println("JWT Filter - Authority: " + (authority != null ? authority.getAuthority() : "null"));

	                UsernamePasswordAuthenticationToken authentication =
	                        new UsernamePasswordAuthenticationToken(
	                                subject,
	                                null,
	                                authority != null ? Collections.singletonList(authority) : Collections.emptyList()
	                        );
	                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

	                SecurityContextHolder.getContext().setAuthentication(authentication);
	                
	                System.out.println("JWT Filter - Authentication set with authorities: " + authentication.getAuthorities());
	            }
	        }

	        filterChain.doFilter(request, response);
	    }


	}
