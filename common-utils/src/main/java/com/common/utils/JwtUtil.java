package com.common.utils;

import com.common.dto.AuthRequest;
import com.common.dto.AuthResponse;
import com.common.exception.InvalidAuthException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

import static com.common.utils.Constants.*;

@Component
public class JwtUtil {

    private final AuthenticationManager authenticationManager;

    public JwtUtil(@Lazy AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Value("${app.api-key}")
    private String appApiKey; // App-level API key

    @Value("${jwt.secret}")
    private String jwtSecret; // JWT signing secret (must be 32+ bytes base64)

    @Value("${jwt.access-expiration}")
    private long accessExpiration; // in seconds

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration; // in seconds

    // ------------------- Token Generation -------------------
    public AuthResponse generateToken(AuthRequest authRequest) {
        validateApiKey(authRequest.getApiKey());

        AuthResponse response = new AuthResponse();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        if (authentication.isAuthenticated()) {
            String accessToken = generateAccessToken(authRequest.getUsername());
            String refreshToken = generateRefreshToken(authRequest.getUsername());

            response.setResult(SUCCESS);
            response.setActive(true);
            response.setAccessToken(accessToken);
            response.setExpiresIn(accessExpiration);
            response.setRefreshToken(refreshToken);
            response.setRefreshExpiresIn(refreshExpiration);
            return response;
        } else {
            throw new InvalidAuthException("User is not authorized. Please check your credentials.", AUTH_401);
        }
    }

    // ------------------- Token Validation -------------------
    public AuthResponse validateToken(String token, String apiKey) {
        validateApiKey(apiKey);

        if (token != null && token.startsWith(BEARER)) {
            token = token.substring(7);
        }

        boolean isExpired = isTokenExpired(token);
        String username = extractUsername(token);

        AuthResponse response = new AuthResponse();
        response.setResult(isExpired ? EXPIRED : VALID);
        response.setActive(!isExpired);
        response.setAccessToken(token);
        response.setExpiresIn(calculateExpireIn(token));
        return response;
    }

    // ------------------- Claims Extraction -------------------
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ------------------- Token Expiration -------------------
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private long calculateExpireIn(String token) {
        long diffMillis = extractExpiration(token).getTime() - System.currentTimeMillis();
        return diffMillis / 1000;
    }

    // ------------------- JWT Token Generation -------------------
    private String generateAccessToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessExpiration * 1000);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration * 1000);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ------------------- Signing Key -------------------
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ------------------- API Key Validation -------------------
    private void validateApiKey(String apiKey) {
        if (!appApiKey.equals(apiKey)) {
            throw new InvalidAuthException("Invalid API Key provided", APIKEY_401);
        }
    }
}
