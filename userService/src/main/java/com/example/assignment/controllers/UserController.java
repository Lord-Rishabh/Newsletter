package com.example.assignment.controllers;

import com.example.assignment.service.UserService;
import com.example.assignment.config.jwt.JwtUtil;
import com.example.assignment.models.UserDTO;
import com.example.assignment.models.User;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController

@RequestMapping(path = "/user")
public class UserController {

  @Autowired
  private JwtUtil jwtUtil;

  @Value("${admin.email}")
  private String adminEmail;

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;

  public UserController (UserService userService, PasswordEncoder passwordEncoder) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/signup")
  public ResponseEntity<String> signup(@RequestBody User user) {
    user.setRole("ROLE_USER");
    if (Objects.equals(user.getEmail(), adminEmail)) {
      user.setRole("ROLE_ADMIN");
    }

    try {
      userService.registerUser(user);
      return ResponseEntity.ok("User registered successfully");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Username or Email already exist");
    }
  }


  @PostMapping("/login")
  public ResponseEntity<Map<String, String>> login (@RequestParam String username,
                                                   @RequestParam String password) {
    if (username.isEmpty() || password.isEmpty()) {
      return ResponseEntity.badRequest().body(Map.of("error",
          "Username and password cannot be empty")
      );
    }

    return userService.findByUsername(username)
        .filter(user -> passwordEncoder.matches(password, user.getPassword()))
        .map(user -> {
          String token = jwtUtil.generateToken(user.getId(), user.getRole());
          return ResponseEntity.ok(Map.of("token", token));
        })
        .orElse(ResponseEntity.badRequest().body(
            Map.of("error",
            "Invalid credentials or user not found"))
        );
  }

  @GetMapping(path = "/getAllUsers")
  public ResponseEntity<List<UserDTO>> getUsers () {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getUserById (@PathVariable Integer id) {
    Optional<User> user = userService.findById(id);
    if (user.isPresent()) {
      UserDTO userDTO = userService.convertToDTO(user.get());
      return ResponseEntity.ok(userDTO);
    } else {
      return ResponseEntity.badRequest().body("User not found with id: " + id);
    }
  }

  @GetMapping("/getUserByJwt")
  public UserDTO getUserByJwtToken(HttpServletRequest request) {

    final String authorizationHeader = request.getHeader("Authorization");

    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      throw new IllegalArgumentException("Authorization header missing or invalid");
    }

    String jwt = authorizationHeader.substring(7);
    Integer userId = jwtUtil.extractUserId(jwt);
    Optional<User> user = userService.findById(userId);

    if (user.isPresent()) {
      return userService.convertToDTO(user.get());
    } else {
      throw new NoSuchElementException("User not found with id: " + userId);
    }
  }

  @DeleteMapping("/deleteUserById/{id}")
  public ResponseEntity<?> deleteUserById (@PathVariable Integer id) {
    Optional<User> user = userService.findById(id);
    if (user.isPresent()) {
      userService.deleteUser(user.get());
      return ResponseEntity.ok("User deleted Successfully");
    } else {
      return ResponseEntity.badRequest().body("User not found with id: " + id);
    }
  }
}
