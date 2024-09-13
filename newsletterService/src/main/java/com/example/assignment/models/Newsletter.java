package com.example.assignment.models;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Time;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "newsletters")
public class Newsletter {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;

  private String description;

  private Double price;

  private LocalDateTime createdAt;

  private Time duration;
}
