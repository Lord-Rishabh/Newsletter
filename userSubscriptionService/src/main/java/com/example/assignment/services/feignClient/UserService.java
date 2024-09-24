package com.example.assignment.services.feignClient;

import com.example.assignment.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired
  private UserServiceClient userServiceClient;

  public User getUserByJwt(String authorizationHeader) {
    try {
      User user = userServiceClient.getUserByJwt(authorizationHeader);
      if (user != null) {
        return user;
      } else {
        throw new Exception("User not found for the given authorization header.");
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to retrieve user from user service", e);
    }
  }

  public User getUserById(Integer userId) {
    try {
      User user = userServiceClient.getUserById(userId);
      if (user != null) {
        return user;
      } else {
        throw new Exception("User not found for the given authorization header.");
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to retrieve user from user service", e);
    }
  }

  public boolean checkAdminRole(String authorizationHeader) {
    User user = userServiceClient.getUserByJwt(authorizationHeader);
    return user != null && "ROLE_ADMIN".equals(user.getRole());
  }
}
