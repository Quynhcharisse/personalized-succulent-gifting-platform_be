package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.models.Account;
import com.exe201.group1.psgp_be.repositories.AccountRepo;
import com.exe201.group1.psgp_be.services.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JWTServiceImpl implements JWTService {

    private final AccountRepo accountRepo;

    @Value("${security-secret-key}")
    private String secretKey;

    @Value("${jwt.expiration.access-token}")
    private long accessExpiration;

    @Value("${jwt.expiration.refresh-token}")
    private long refreshExpiration;

    @Override
    public String extractEmailFromJWT(String jwt) {
        return getClaim(jwt, Claims::getSubject);
    }

    private <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaimsFromToken(token);
        if (claims == null) {
            return null;
        }
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaimsFromToken(String token) {
        if (token.split("\\.").length != 3) {
            return null;
        }
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return null;
        }
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    @Override
    public String generateAccessToken(UserDetails user) {
        Account account = (Account) user;
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", account.getRole());
        return generateToken(claims, user, accessExpiration);
    }


    @Override
    public String generateRefreshToken(UserDetails user) {
        Account account = (Account) user;
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", account.getRole());
        return generateToken(claims, user, refreshExpiration);
    }

    private String generateToken(Map<String, Object> extractClaims, UserDetails user, long expiredTime) {
        return Jwts.builder()
                .setClaims(extractClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiredTime))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public boolean checkIfNotExpired(String jwt) {
        Date expiration = getClaim(jwt, Claims::getExpiration);
        return expiration != null && !Objects.requireNonNull(expiration).before(new Date());
    }
}
