package com.example.assignment.controller;

import com.example.assignment.models.Newsletter;
import com.example.assignment.models.UserSubscription;
import com.example.assignment.services.UserSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
public class UserSubscriptionController {

  @Autowired
  private UserSubscriptionService userSubscriptionService;

  @PostMapping("/subscribe/{newsletterId}")
  public ResponseEntity<String> subscribeToNewsletter(
      @RequestHeader("Authorization") String authorizationHeader,
      @PathVariable Integer newsletterId) {

    userSubscriptionService.subscribe(authorizationHeader, newsletterId);
    return ResponseEntity.ok("Subscribed successfully!");
  }

  @GetMapping("/user")
  public ResponseEntity<List<UserSubscription>> getAllSubscriptionsForUser(
      @RequestHeader("Authorization") String authorizationHeader) {

    List<UserSubscription> subscriptions = userSubscriptionService.getAllSubscriptionsForUser(authorizationHeader);
    return ResponseEntity.ok(subscriptions);
  }

  @PostMapping("/renew/{subscriptionId}")
  public ResponseEntity<String> renewSubscription(
      @RequestHeader("Authorization") String authorizationHeader,
      @PathVariable Long subscriptionId) {

    userSubscriptionService.renewSubscription(authorizationHeader, subscriptionId);
    return ResponseEntity.ok("Subscription renewed successfully!");
  }

  @PostMapping("/send/{newsletterId}")
  public String sendNewsletterToSubscribers(
      @RequestHeader("Authorization") String authorizationHeader,
      @PathVariable Integer newsletterId) {
    userSubscriptionService.sendNewsletterToSubscribedUsers(newsletterId, authorizationHeader);
    return "Newsletter sent to all subscribed users!";
  }
}

