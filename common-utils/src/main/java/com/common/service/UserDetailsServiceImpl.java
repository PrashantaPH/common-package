package com.common.service;

import com.common.config.CustomUserDetails;
import com.common.dto.CommonUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CommonUserManagementService commonUserManagementService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        CommonUserDetails userDetails = commonUserManagementService.findByEmail(email);
        return new CustomUserDetails(userDetails);
    }
}
