package com.example.assignment.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"email"}),
    @UniqueConstraint(columnNames = {"username"})
})
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank(message = "Name cannot be empty")
  @Size(max = 30, min = 3, message = "Name should be in the range between 3-30 characters")
  private String name;

  @NotBlank(message = "Name cannot be empty")
  @Size(max = 30, min = 3, message = "Name should be in the range between 3-30 characters")
  private String username;

  @NotBlank(message = "Email cannot be empty")
  @Email(message = "Enter a valid Email")
  private String email;

  @NotBlank(message = "Password cannot be empty")
  private String password;

  private String role;
}
