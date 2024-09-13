package com.example.assignment.models;

import lombok.Data;

@Data
public class UserDto {

  private Integer id;

  private String name;

  private String username;

  private String email;

  private String role;
}
