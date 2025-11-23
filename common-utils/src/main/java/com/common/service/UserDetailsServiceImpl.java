
package com.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: Replace with DB lookup. For demo, use a stable encoded password:
        String rawPassword = "password";
        String encoded = passwordEncoder.encode(rawPassword); // If you re-encode on every call, BCrypt salt changes.

        // Better: store a pre-encoded value or fetch from DB.
        // Example hardcoded stable bcrypt (generated once):
        // String encoded = "$2a$10$7vLzQnUuQG8g2zqM3vLJ0Oe8C9yJ7kPqkVd1v8v3d1x7tN7m2YJ4a"; // matches "password"

        return User.builder()
                .username(username)
                .password(encoded)
                .roles("USER")
                .build();
    }
}
