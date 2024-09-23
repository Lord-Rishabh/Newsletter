package com.example.assignment.services;

import com.example.assignment.models.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "userService", url = "${userService.url}")
public interface UserServiceClient {
  @GetMapping("/user/getUserByJwt")
  User getUserByJwt(@RequestHeader("Authorization") String authorizationHeader);

  @GetMapping("/user/{id}")
  User getUserById(@PathVariable Integer id);
}