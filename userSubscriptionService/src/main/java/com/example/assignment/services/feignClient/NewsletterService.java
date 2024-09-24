package com.example.assignment.services.feignClient;

import com.example.assignment.models.Newsletter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NewsletterService {

  @Autowired
  private NewsletterServiceClient newsletterServiceClient;

  public Newsletter getNewsletter(Integer id) {
    try {
      Newsletter newsletter = newsletterServiceClient.getNewsletterById(id);
      if (newsletter != null) {
        return newsletter;
      } else {
        throw new Exception("Newsletter not found for the given id.");
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to retrieve the newsletter");
    }
  }
}
