package com.example.authserver.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Created by tomo on 2019-02-10.
 */
public class UserAuthentication implements Authentication, Serializable {
    private UserPrincipal userPrincipal;
    private List<? extends GrantedAuthority> authorities;
    private Object credentials;
    private UserAuthenticationDetails details;
    private boolean authenticated;

    public UserAuthentication(UserPrincipal principal,
                              List<? extends GrantedAuthority> authorities,
                              UserAuthenticationDetails details) {
        this(principal, authorities, null, details, true);
    }

    public UserAuthentication(UserPrincipal principal,
                              List<? extends GrantedAuthority> authorities,
                              Object credentials,
                              UserAuthenticationDetails details,
                              boolean authenticated) {
        this.userPrincipal = principal;
        this.authorities = authorities;
        this.credentials = credentials;
        this.details = details;
        this.authenticated = authenticated;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getDetails() {
        return details;
    }

    @Override
    public Object getPrincipal() {
        return userPrincipal;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = authenticated;
    }

    @Override
    public String getName() {
        return userPrincipal.getName();
    }
}
