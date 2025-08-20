package com.splitwise.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JWTAuthenticate extends AbstractAuthenticationToken {


    private String token;

    public JWTAuthenticate(String token){
        super(null);
        this.token = token;
        setAuthenticated(false);
    }
    public JWTAuthenticate(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
