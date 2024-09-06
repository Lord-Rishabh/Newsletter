package com.example.assignment.Service;

import com.example.assignment.models.User;
import com.example.assignment.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

  @Autowired
  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public User registerUser (User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

}


//  @Autowired
//  private PasswordEncoder passwordEncoder;
//
//  @Autowired
//  private JwtUtil jwtUtil;
//

//
//  public String login (String username, String password) throws Exception {
//    Optional<User> userOptional = Optional.ofNullable(userRepository.findByUsername(username));
//    if (userOptional.isPresent()) {
//      User user = userOptional.get();
//      if (passwordEncoder.matches(password, user.getPassword())) {
//        return jwtUtil.generateToken(user.getEmail());
//      } else {
//        throw new Exception("Invalid credentials");
//      }
//    } else {
//      throw new Exception("User not found");
//    }
//  }
