package com.example.assignment.config.jwt;

import com.example.assignment.models.User;
import com.example.assignment.service.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  @Lazy
  @Autowired
  private final UserService userService;

  @Lazy
  public JwtAuthenticationFilter(JwtUtil jwtUtil, UserService userService) {
    this.jwtUtil = jwtUtil;
    this.userService = userService;
  }

  @Override
  protected void doFilterInternal (HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain)
      throws ServletException, IOException {
    final String authorizationHeader = request.getHeader("Authorization");

    Integer userId = null;
    String jwt = null;
    String role = null;

    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      jwt = authorizationHeader.substring(7);
      try {
        userId = jwtUtil.extractUserId(jwt);  // Extract the user ID from the JWT
        role = jwtUtil.extractRole(jwt);
      } catch (JwtException e) {
        logger.error("JWT token is invalid", e);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("JWT token is invalid");
        return;
      }
    }

    if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      Optional<User> optionalUser = userService.findById(userId);
      if (optionalUser.isPresent() && jwtUtil.validateToken(jwt, userId)) {
        User user = optionalUser.get();
        var authToken = new UsernamePasswordAuthenticationToken(
            user,
            null,
            Collections.singletonList(new SimpleGrantedAuthority(role))
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }

    filterChain.doFilter(request, response);
  }
}
