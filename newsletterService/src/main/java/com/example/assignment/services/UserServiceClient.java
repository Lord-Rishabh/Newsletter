package com.example.assignment.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "userService", url = "${userService.url}")
public interface UserServiceClient {
  @GetMapping("/user/getUserByJwt")
  ResponseEntity<?> getUserByJwt(@RequestHeader("Authorization") String authorizationHeader);
}