package ru.zuzex.practice.taskms.security.jwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.zuzex.practice.taskms.security.JwtUser;

import java.io.IOException;

public class JwtTokenFilter extends OncePerRequestFilter {
    private final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);

    private final JwtTokenUtils jwtTokenUtils;

    public JwtTokenFilter(JwtTokenUtils jwtTokenUtils) {
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Missing or invalid Authorization header");
//                sendError(response, "Missing or invalid token", HttpStatus.UNAUTHORIZED);
                return;
            }

            String token = authHeader.substring(7);
            if (token.isBlank()) {
                logger.warn("Empty JWT token");
//                sendError(response, "Empty token", HttpStatus.UNAUTHORIZED);
                return;
            }

            if (!jwtTokenUtils.isValid(token)) {
                logger.warn("Invalid JWT token");
//                sendError(response, "Invalid token", HttpStatus.FORBIDDEN);
                return;
            }

            var jwtUser = new JwtUser(
                    jwtTokenUtils.getId(token),
                    jwtTokenUtils.getUsername(token),
                    jwtTokenUtils.getRoles(token)
            );

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    jwtUser, null, jwtUser.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            logger.error("JWT validation error", ex);
//            sendError(response, "JWT processing error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void sendError(HttpServletResponse response, String message, HttpStatus status)
            throws IOException {
        response.setStatus(status.value());
        response.getWriter().write(message);
        response.setContentType("text/plain");
    }
}
