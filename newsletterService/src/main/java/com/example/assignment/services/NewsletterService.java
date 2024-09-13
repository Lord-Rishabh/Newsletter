package com.example.assignment.services;

import com.example.assignment.models.Newsletter;
import com.example.assignment.models.UserDto;
import com.example.assignment.repository.NewsletterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Service
public class NewsletterService {

  @Autowired
  private NewsletterRepository newsletterRepository;

  @Autowired
  private UserServiceClient userServiceClient;

  public void addNewsletter (Newsletter newsletter) {
    newsletterRepository.save(newsletter);
  }

  public Newsletter getNewsletterById (Integer id) {
    Optional<Newsletter> optionalNewsletter = newsletterRepository.findById(id);
    return optionalNewsletter.orElse(null);
  }

  public List<Newsletter> getAllNewsletters () {
    return (List<Newsletter>) newsletterRepository.findAll();
  }

  public void deleteNewsletter (Integer id) {
    newsletterRepository.deleteById(id);
  }

  public boolean checkAdminRole(String authorizationHeader) {
    ResponseEntity<?> response = userServiceClient.getUserByJwt(authorizationHeader);

    if (response.getStatusCode().is2xxSuccessful()) {
      Object responseBody = response.getBody();

      if (responseBody instanceof LinkedHashMap) {
        LinkedHashMap<String, Object> userMap = (LinkedHashMap<String, Object>) responseBody;

        String role = (String) userMap.get("role");
        return "ROLE_ADMIN".equals(role);
      }
    }
    return false;
  }
}