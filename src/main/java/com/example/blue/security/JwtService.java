package com.example.blue.security;

import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    private final SecretKey secretKey=Keys.hmacShaKeyFor("mysecretkeymysecretkeymysecretkeymysecretkey".getBytes());

    public String generateToken(String Username) {
        return Jwts.builder()
                .subject(Username)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                +1000*60*60
                        )
                )
                .signWith(secretKey)
                .compact();
    }

    private boolean isTokenExpired(String token) {

        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token,
        Claims::getExpiration);
    }

    public  String extractUsername(String token) {
        return extractClaims(token,Claims::getSubject);
    }

    public boolean validateToken(String token,String username) {
        String extractedUsername=extractUsername(token);
        return extractedUsername.equals(username)&&!isTokenExpired(token);
    }
    public <T> T extractClaims(String token, Function<Claims,T> claimsResolver) {
        final Claims claims=extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
