package ru.zuzex.practice.taskms.security.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.zuzex.practice.taskms.security.Role;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class JwtTokenUtils {
    private SecretKey secretKey;
    @Value("${security.jwt.secret}")
    private String secret;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public boolean isValid(String token) {
        Jws<Claims> claims = Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
        return claims.getPayload().getExpiration().after(new Date());
    }

    public UUID getId(String token) {
        return UUID.fromString(getClaims(token).get("id", String.class));
    }

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    public Set<Role> getRoles(String token) {
        return ((List<?>) getClaims(token).get("roles")).stream()
                .map(authority -> Role.valueOf((String) authority))
                .collect(Collectors.toSet());
    }

    private Claims getClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
