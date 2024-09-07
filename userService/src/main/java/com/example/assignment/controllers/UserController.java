package com.example.assignment.controllers;

import com.example.assignment.Service.UserService;
import com.example.assignment.config.jwt.JwtUtil;
import com.example.assignment.models.UserDTO;
import com.example.assignment.repositories.UserRepository;
import com.example.assignment.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController

@RequestMapping(path = "/user")
public class UserController {

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private UserRepository userRepository;

  @Value("${admin.email}")
  private String adminEmail;

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;

  public UserController(UserService userService, PasswordEncoder passwordEncoder) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/signup")
  public ResponseEntity<String> signup(@RequestBody User user) {
    if (userService.findByEmail(user.getEmail()).isPresent()) {
      return ResponseEntity.badRequest().body("Email already in use");
    }

    if (userService.findByEmail(user.getUsername()).isPresent()) {
      return ResponseEntity.badRequest().body("Username already in use");
    }

    user.setRole("ROLE_USER");
    if(Objects.equals(user.getEmail(), adminEmail)) {
      user.setRole("ROLE_ADMIN");
    }

    userService.registerUser(user);

    return ResponseEntity.ok("User registered successfully");
  }

  @PostMapping("/login")
  public ResponseEntity<Map<String, String>> login(@RequestParam String username,
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
  public ResponseEntity<List<UserDTO>> getUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getUserById(@PathVariable Integer id) {
    Optional<User> user = userService.findById(id);
    if (user.isPresent()) {
      UserDTO userDTO = userService.convertToDTO(user.get());
      return ResponseEntity.ok(userDTO);
    } else {
      return ResponseEntity.badRequest().body("User not found with id: " + id);
    }
  }
}
