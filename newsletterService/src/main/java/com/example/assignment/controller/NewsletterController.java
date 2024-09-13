package com.example.assignment.controller;

import com.example.assignment.models.Newsletter;
import com.example.assignment.services.NewsletterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: Only admins can add newsletters");
      }

      newsletterService.addNewsletter(newsletter);
      return ResponseEntity.ok("Newsletter added successfully");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Error: " + e.getMessage());
    }
  }

  @GetMapping("/getAll")
  public ResponseEntity<?> getAllNewsletters () {
    try {
      List<Newsletter> newsletters = newsletterService.getAllNewsletters();
      return ResponseEntity.ok(newsletters);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to retrieve newsletters: " + e.getMessage());
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getNewsletterById (@PathVariable Integer id) {
    try {
      Newsletter newsletter = newsletterService.getNewsletterById(id);
      if (newsletter != null) {
        return ResponseEntity.ok(newsletter);
      } else {
        return ResponseEntity.notFound().build();
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to retrieve newsletter: " + e.getMessage());
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
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to delete newsletter: " + e.getMessage());
    }
  }
}
