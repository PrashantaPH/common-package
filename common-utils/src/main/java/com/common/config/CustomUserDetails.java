package com.common.config;

import com.common.dto.CommonUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final CommonUserDetails userDetails;

    public CustomUserDetails(CommonUserDetails commonUserDetails) {
        this.userDetails = commonUserDetails;
    }

    public String getUserId() {
        return userDetails.getUserId();
    }

    public String getFullName() {
        return userDetails.getFullName();
    }

    public String getStatus() {
        return userDetails.getStatus();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userDetails.getRole()));
    }

    @Override
    public String getPassword() {
        return userDetails.getPassword();
    }

    @Override
    public String getUsername() {
        return userDetails.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !"LOCKED".equalsIgnoreCase(userDetails.getStatus());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "ACTIVE".equalsIgnoreCase(userDetails.getStatus());
    }
}