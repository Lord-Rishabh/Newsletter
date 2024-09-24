package com.example.assignment.services.FeignClient;

import com.example.assignment.models.Newsletter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "newsletterService", url = "${newsletterService.url}")
public interface NewsletterServiceClient {
  @GetMapping("/newsletter/{id}")
  Newsletter getNewsletterById (@PathVariable Integer id);
}
