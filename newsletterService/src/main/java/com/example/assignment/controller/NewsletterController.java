package com.example.assignment.controller;

import com.example.assignment.models.Newsletter;
import com.example.assignment.services.NewsletterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/newsletter")
public class NewsletterController {

  @Autowired
  private NewsletterService newsletterService;

  @PostMapping(path = "/add")
  public ResponseEntity<String> addNewsletter (@RequestHeader("Authorization") String authorizationHeader,
                                              @RequestBody Newsletter newsletter) {
    try {
      boolean isAdmin = newsletterService.checkAdminRole(authorizationHeader);

      if (!isAdmin) {
        return ResponseEntity.badRequest().body("Forbidden: Only admins can add newsletters");
      }

      newsletterService.addNewsletter(newsletter);
      return ResponseEntity.ok("Newsletter added successfully");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Error: " + e.getMessage());
    }
  }

  @GetMapping("/getAll")
  public List<Newsletter> getAllNewsletters() {
    try {
      return newsletterService.getAllNewsletters();
    } catch (Exception e) {
      throw new RuntimeException("Failed to retrieve newsletters: " + e.getMessage(), e);
    }
  }

  @GetMapping("/{id}")
  public Newsletter getNewsletterById (@PathVariable Integer id) {
    try {
      Newsletter newsletter = newsletterService.getNewsletterById(id);
      if (newsletter == null) {
        throw new NoSuchElementException("Newsletter not found with id: " + id);
      }
      return newsletter;
    } catch (Exception e) {
      throw new RuntimeException("Failed to retrieve newsletter: " + e.getMessage(), e);
    }
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<String> deleteNewsletter (@RequestHeader("Authorization") String authorizationHeader,
                                                 @PathVariable Integer id) {
    try {
      boolean isAdmin = newsletterService.checkAdminRole(authorizationHeader);

      if (!isAdmin) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: Only admins can delete newsletters");
      }

      newsletterService.deleteNewsletter(id);
      return ResponseEntity.ok("Newsletter deleted successfully");
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body("Failed to delete newsletter: " + e.getMessage());
    }
  }
}
