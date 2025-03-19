package com.libraryManagementArangoDB.utills;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.libraryManagementArangoDB.dto.UserInfoDTO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512); // ✅ Secure key
    private final long jwtExpirationMs = 86400000; // 24 hours

    // Generate Token with custom claims (id, email, roles)
    public String generateToken(Authentication authentication) {
        UserInfoDTO user = (UserInfoDTO) authentication.getPrincipal(); // Assuming your principal is UserInfoDTO

        return Jwts.builder()
                .setSubject(user.getEmail()) // You can set username as subject
                .claim("id", user.getId()) // Store user id
                .claim("userName", user.getUserName())
                .claim("email", user.getEmail()) // Store email
                .claim("roles",
                        user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())) // Store
                                                                                                                         // roles
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key) // Use proper key
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.get("roles", List.class); // Get roles as list
    }

}
