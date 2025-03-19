package com.libraryManagementArangoDB.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryManagementArangoDB.config.CustomResponse;
import com.libraryManagementArangoDB.utills.JwtTokenProvider;
import com.libraryManagementArangoDB.utills.UserInfo;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserInfo userInfo;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = null;
        String email = null;
        String header = request.getHeader("Authorization");

        try {
            // Extract token from header
            if (header != null && header.startsWith("Bearer ")) {
                token = header.substring(7);
                email = jwtTokenProvider.getEmailFromToken(token); // Might throw ExpiredJwtException
            }

            System.out.println("email: " + email);

            // Authenticate the user if email is extracted and not already authenticated
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userInfo.loadUserByUsername(email);
                if (jwtTokenProvider.validateToken(token)) {
                    UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    userToken.setDetails(new WebAuthenticationDetails(request));
                    request.setAttribute("user", userDetails);
                    SecurityContextHolder.getContext().setAuthentication(userToken);
                }
            }

            // Continue the filter chain
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException ex) {
            // Handle the ExpiredJwtException
            System.out.println("JWT expired: " + ex.getMessage());
            handleJwtException(response, "JWT token has expired. Please log in again.",
                    HttpStatus.UNAUTHORIZED.value());
        } catch (Exception ex) {
            // Handle any other exceptions
            System.out.println("Error in JWT processing: " + ex.getMessage());
            handleJwtException(response, "An error occurred while processing your request.",
                    HttpStatus.BAD_REQUEST.value());
        }

    }

    /**
     * Helper method to handle JWT exceptions and send custom error responses.
     */
    private void handleJwtException(HttpServletResponse response, String errorMessage, int statusCode)
            throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        CustomResponse<String> customResponse = new CustomResponse<>(errorMessage,
                "JWT_ERROR",
                statusCode,
                null, null);
        // Serialize the custom response object to JSON and write it to the response
        response.getWriter().write(new ObjectMapper().writeValueAsString(customResponse));
        response.getWriter().flush();
    }

}
