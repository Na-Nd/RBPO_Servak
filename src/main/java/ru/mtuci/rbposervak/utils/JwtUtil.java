package ru.mtuci.rbposervak.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.mtuci.rbposervak.entities.User;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-jwt.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-jwt.expiration}")
    private long refreshTokenExpiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    ///  Генерация access
    public String generateAccessToken(User user){
        Map<String, Object> claims = new HashMap<>();

        claims.put("role", user.getRole().name());
        claims.put("token_type", "access");

        return createToken(claims, user.getUsername(), accessTokenExpiration);
    }

    /// Генерация refresh
    public String generateRefreshToken(User user){
        Map<String, Object> claims = new HashMap<>();

        //claims.put("role", user.getRole().name());
        claims.put("token_type", "refresh");

        return createToken(claims, user.getUsername(), refreshTokenExpiration);
    }

    ///  Создание токена
    private String createToken(Map<String, Object> claims, String subject, long expiration){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /// Валидация истечения
    public boolean validateExpirationToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (ExpiredJwtException e){
            return false;
        }
    }

    /// Общая валидация (реагируем на любое исключение)
    public boolean validateToken(String token) {
        try{
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (Exception e){
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Set<GrantedAuthority> extractAuthorities(String token) {
        List<?> roles = extractClaim(token, claims -> claims.get("role", List.class));
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority((String) role))
                .collect(Collectors.toSet());
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String token, UserDetails userDetails) {
        Set<GrantedAuthority> authorities = extractAuthorities(token);
        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }

    /// Отсечь Bearer_ от запроса
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /// Отсечь Bearer_ от строки (перегрузка)
    public String resolveToken(String bearerToken){
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /// Извлечение username пользователя
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    /// Извлечение роли пользователя
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
}