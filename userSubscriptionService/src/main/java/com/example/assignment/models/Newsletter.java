package com.example.assignment.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Entity
public class Newsletter {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String title;

  private String description;

  private Double price;

  private LocalDateTime createdAt;

  private Duration subscriptionPeriod;
}
