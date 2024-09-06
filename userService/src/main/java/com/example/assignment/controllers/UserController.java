package com.example.assignment.controllers;

import com.example.assignment.Service.UserService;
import com.example.assignment.config.jwt.JwtUtil;
import com.example.assignment.repositories.UserRepository;
import com.example.assignment.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
@RestController

@RequestMapping(path = "/user")
public class UserController {

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private UserRepository userRepository;

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
    userService.registerUser(user);
    return ResponseEntity.ok("User registered successfully");
  }

  @PostMapping("/login")
  public ResponseEntity<String> login (@RequestParam(required = false) String username,
                                      @RequestParam(required = false) String password) {
    if (username == null || username.isEmpty()) {
      return ResponseEntity.badRequest().body("Username can't be null or empty" + username + " " + password);
    }

    if (password == null || password.isEmpty()) {
      return ResponseEntity.badRequest().body("Password can't be null or empty" + username + " " + password);
    }

    return userService.findByUsername(username)
        .map(user -> {
          if (passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtUtil.generateToken(user.getUsername());
            return ResponseEntity.ok("Login successful, Token: " + token);
          } else {
            return ResponseEntity.badRequest().body("Invalid credentials" + username + " " + password);
          }
        })
        .orElse(ResponseEntity.badRequest().body("User not found " + username + " " + password));
  }



  @GetMapping(path = "/all")
  public @ResponseBody Iterable<User> getUsers () {
    return userRepository.findAll();
  }
}
