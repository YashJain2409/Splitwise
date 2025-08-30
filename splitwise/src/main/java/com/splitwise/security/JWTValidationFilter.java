package com.splitwise.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTValidationFilter extends OncePerRequestFilter {

	private AuthenticationManager authenticationManager;
	private JWTUtil jwtUtil;

	public JWTValidationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
		this.authenticationManager = authenticationManager;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String token = request.getHeader("Authorization");

		System.out.println("token " + token);

		if (token != null) {
			token = token.substring(7);

			JWTAuthenticate jwtAuthenticate = new JWTAuthenticate(token);

			Authentication authentication = authenticationManager.authenticate(jwtAuthenticate);
			if (authentication.isAuthenticated()) {
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		filterChain.doFilter(request, response);
	}
}
