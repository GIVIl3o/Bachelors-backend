package com.example.bachelor.rest;

import com.example.bachelor.api.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
@AllArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final UserService service;

    private void authenticate(UserDetails user, HttpServletRequest request) {
        var authentication = new UsernamePasswordAuthenticationToken(user, null, List.of());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filter)
            throws IOException, ServletException {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filter.doFilter(request, response);
            return;
        }

        var header = request.getHeader("Authorization") == null ? "" : request.getHeader("Authorization");
        var jwt = header.startsWith("Bearer ") ? header.substring(7) : null;

        service.parseToken(jwt).ifPresent(user -> authenticate(user, request));

        filter.doFilter(request, response);
    }


}
