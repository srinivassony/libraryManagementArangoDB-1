package com.libraryManagementArangoDB.utills;

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

@Component
public class JwtTokenProvider {

    private final String jwtSecret = "libraryManagementArangoDBSecretKey";
    private final long jwtExpirationMs = 86400000; // 24 hours

    // Generate Token with custom claims (id, email, roles)
    public String generateToken(Authentication authentication) {
        UserInfoDTO user = (UserInfoDTO) authentication.getPrincipal(); // Assuming your principal is UserInfoDTO

        return Jwts.builder().setSubject(user.getEmail()) // You can set username as subject
                .claim("id", user.getId()) // Store user id
                .claim("userName", user.getUserName()).claim("email", user.getEmail()) // Store email
                .claim("roles",
                        user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())) // Store
                                                                                                                         // roles
                .setIssuedAt(new Date()).setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();

        return (List<String>) claims.get("roles"); // Roles stored as a list in JWT
    }

}
