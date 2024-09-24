package com.example.assignment.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "userSubscriptions")
public class UserSubscription implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull(message = "userId cannot be null")
  private Integer userId;

  @NotNull(message = "newsletterId cannot be null")
  private Integer newsletterId;

  private LocalDate subscriptionStartDate;
  private LocalDate subscriptionEndDate;

  private boolean isActive;
}
