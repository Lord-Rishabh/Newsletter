package com.example.assignment.services;

import com.example.assignment.models.Newsletter;
import com.example.assignment.models.User;
import com.example.assignment.models.UserSubscription;
import com.example.assignment.repository.UserSubscriptionRepository;
import com.example.assignment.services.FeignClient.NewsletterService;
import com.example.assignment.services.FeignClient.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserSubscriptionService {

  @Autowired
  private UserSubscriptionRepository userSubscriptionRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private NewsletterService newsletterService;

  @Transactional
  public void subscribe(String authorizationHeader, Integer newsletterId) {
    User user = userService.getUserByJwt(authorizationHeader);
    Newsletter newsletter = newsletterService.getNewsletter(newsletterId);

    if(newsletter == null) {
      throw new RuntimeException("Invalid Newsletter Id");
    }

    Optional<UserSubscription> existingSubscription = userSubscriptionRepository
        .findByUserIdAndNewsletterId(user.getId(), newsletter.getId());

    if (existingSubscription.isPresent()) {
      throw new RuntimeException("UserSubscription already exists");
    }

    LocalDateTime startDate = LocalDateTime.now();
    LocalDateTime endDate = startDate.plus(newsletter.getSubscriptionPeriod());

    UserSubscription userSubscription = UserSubscription.builder()
        .userId(user.getId())
        .newsletterId(newsletter.getId())
        .subscriptionStartDate(startDate.toLocalDate())
        .subscriptionEndDate(endDate.toLocalDate())
        .isActive(true)
        .build();

    userSubscriptionRepository.save(userSubscription);
  }

  public List<Integer> getUsersSubscribedToNewsletter(Integer newsletterId) {
    List<UserSubscription> subscriptions = userSubscriptionRepository.findByNewsletterId(newsletterId);

    return subscriptions.stream()
        .map(UserSubscription::getUserId)
        .collect(Collectors.toList());
  }

  public List<UserSubscription> getAllSubscriptionsForUser(String authorizationHeader) {
    User user = userService.getUserByJwt(authorizationHeader);
    return userSubscriptionRepository.findByUserId(user.getId());
  }

  @Transactional
  public void renewSubscription(String authorizationHeader, Long subscriptionId) {
    User user = userService.getUserByJwt(authorizationHeader);
    UserSubscription subscription = userSubscriptionRepository.findById(subscriptionId)
        .orElseThrow(() -> new RuntimeException("Subscription not found"));

    if(!Objects.equals(subscription.getUserId(), user.getId())) {
      throw new RuntimeException("You are not authorized");
    }
    if (!subscription.isActive()) {
      throw new RuntimeException("Cannot renew an inactive subscription");
    }

    Newsletter newsletter = newsletterService.getNewsletter(subscription.getNewsletterId());
    LocalDateTime newEndDate = subscription.
        getSubscriptionEndDate().
        atStartOfDay().
        plus(newsletter.getSubscriptionPeriod());

    subscription.setSubscriptionEndDate(newEndDate.toLocalDate());
    userSubscriptionRepository.save(subscription);
  }
}
