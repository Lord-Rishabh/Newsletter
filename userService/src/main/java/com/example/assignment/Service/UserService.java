package com.example.assignment.service;

import com.example.assignment.models.User;
import com.example.assignment.models.UserDTO;
import com.example.assignment.models.UserMapper;
import com.example.assignment.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

  public void registerUser(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userRepository.save(user);
  }

  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  @Cacheable(value = "users", key = "#id")
  public Optional<User> findById(Integer id) {
    return userRepository.findById(id);
  }

  public UserDTO convertToDTO(User user) {
    return UserMapper.INSTANCE.userToUserDTO(user);
  }

  public List<UserDTO> getAllUsers() {
    List<UserDTO> users = new ArrayList<>();
    for (User user : userRepository.findAll()) {
      users.add(UserMapper.INSTANCE.userToUserDTO(user));
    }
    return users;
  }

  @CacheEvict(value = "users", key = "#user.id")
  public void deleteUser(User user) {
    userRepository.delete(user);
  }
}
