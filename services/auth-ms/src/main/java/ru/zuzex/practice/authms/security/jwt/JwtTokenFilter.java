package ru.zuzex.practice.authms.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class JwtTokenFilter extends GenericFilterBean {
    private final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);

    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        String bearerToken = ((HttpServletRequest) servletRequest).getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer "))
            bearerToken = bearerToken.substring(7);

        try {
            if (bearerToken != null && jwtTokenProvider.isValid(bearerToken)) {
                Authentication auth = jwtTokenProvider.getAuthentication(bearerToken);
                if (auth != null)
                    SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception ignored) {
            logger.error(ignored.getMessage(), ignored);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}