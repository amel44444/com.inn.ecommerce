package com.inn.ecommerce.JWT;

import com.inn.ecommerce.utils.JwtRequestUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomerUsersDetailsService usersDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        logger.debug("Request received at path: " + path);

        // Bypass filter for login and signup endpoints
        if (path.contains("/user/login") || path.contains("/user/signup")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Authorization header is missing or does not start with 'Bearer '");
                respondWithError(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid token");
                return;
            }

            String token = authHeader.substring(7);

            if (!jwtUtil.validateToken(token)) {
                logger.warn("JWT token is invalid");
                respondWithError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }

            String email = jwtUtil.extractUsername(token);
            UserDetails userDetails = usersDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            respondWithError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token has expired");
        } catch (Exception e) {
            respondWithError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Authentication error");
        } finally {
            JwtRequestUtil.clear(); // Nettoyage des données de la requête JWT (utile si stockées dans ThreadLocal)
        }
    }

    private void respondWithError(HttpServletResponse response, int statusCode, String message)
            throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
