package com.example.assignment.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class EmailMessage implements Serializable {
  private String to;
  private String subject;
  private String body;
}
