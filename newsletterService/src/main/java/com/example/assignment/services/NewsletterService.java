package com.example.assignment.services;

import com.example.assignment.models.Newsletter;
import com.example.assignment.models.User;
import com.example.assignment.repository.NewsletterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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

  @Cacheable(value = "newsletters", key = "#id")
  public Newsletter getNewsletterById (Integer id) {
    Optional<Newsletter> optionalNewsletter = newsletterRepository.findById(id);
    return optionalNewsletter.orElse(null);
  }

  public List<Newsletter> getAllNewsletters () {
    return (List<Newsletter>) newsletterRepository.findAll();
  }

  @CacheEvict(value = "newsletters", key = "#id")
  public void deleteNewsletter (Integer id) {
    newsletterRepository.deleteById(id);
  }


  public boolean checkAdminRole(String authorizationHeader) {
    User user = userServiceClient.getUserByJwt(authorizationHeader);

    if (user != null) {
      String role = user.getRole();
      return "ROLE_ADMIN".equals(role);
    }
    return false;
  }

}