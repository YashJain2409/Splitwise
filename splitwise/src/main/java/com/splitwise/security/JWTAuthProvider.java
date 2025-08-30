package com.splitwise.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public class JWTAuthProvider implements AuthenticationProvider {

	private JWTUtil jwtUtil;
	private UserDetailsService userDetailsService;

	public JWTAuthProvider(JWTUtil jwtUtil, UserDetailsService userDetailsService) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		JWTAuthenticate jwtAuthenticate = (JWTAuthenticate) authentication;
		String token = jwtAuthenticate.getToken();
		if (token != null) {

			String username = jwtUtil.validateJwtToken(token);

			if (username != null) {

				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				return new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList());
			}
		}
		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return JWTAuthenticate.class.isAssignableFrom(authentication);
	}
}
