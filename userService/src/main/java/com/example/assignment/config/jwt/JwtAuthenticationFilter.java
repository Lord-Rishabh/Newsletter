package com.example.assignment.config.jwt;

import com.example.assignment.Service.UserService;
import com.example.assignment.models.User;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
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
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    final String authorizationHeader = request.getHeader("Authorization");

    String username = null;
    String jwt = null;

    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      jwt = authorizationHeader.substring(7);
      try {
        username = jwtUtil.extractUsername(jwt);
      } catch (JwtException e) {
        logger.error("JWT token is invalid", e);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("JWT token is invalid");
        return;
      }
    }

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      Optional<User> optionalUser = userService.findByUsername(username);
      if (optionalUser.isPresent() && jwtUtil.validateToken(jwt, username)) {
        User user = optionalUser.get();
        var authToken = new UsernamePasswordAuthenticationToken(user, null, null);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }

    filterChain.doFilter(request, response);
  }
}
