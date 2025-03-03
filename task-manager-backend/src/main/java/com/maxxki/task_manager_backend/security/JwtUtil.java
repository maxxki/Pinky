package com.maxxki.task_manager_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;

// **Hinzugefügte Imports für getAuthentication Methode:**
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;


@Component
public class JwtUtil {

    private static final String SECRET_KEY = "MeinSuperSichererGeheimerSchlüsselFürJWTs1234567890!";
    private static final long EXPIRATION_TIME = 86400000; // 1 Tag

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        return getClaims(token).getExpiration().after(new Date());
    }

    public String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // **Hinzugefügte getAuthentication Methode:**
    public Authentication getAuthentication(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        // Hier könnten weitere Berechtigungsprüfungen erfolgen, falls nötig
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null, // Keine Credentials benötigt, da JWT validiert
                userDetails.getAuthorities() // Rollen/Berechtigungen des Users
        );
    }
}