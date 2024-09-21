package com.example.assignment.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "newsletters")
public class Newsletter {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Size(min=3, max=20, message = "title should be between 3-30 characters")
  private String title;

  @Size(min=3, max=100, message = "title should be between 3-30 characters")
  private String description;

  @NotNull(message = "Price cannot be null")
  @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
  private Double price;

  private LocalDateTime createdAt;

  private Duration subscriptionPeriod;
}
