package com.example.assignment.services;

import com.example.assignment.models.Newsletter;
import com.example.assignment.models.User;
import com.example.assignment.models.UserSubscription;
import com.example.assignment.repository.UserSubscriptionRepository;
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
  private UserServiceClient userServiceClient;

  @Autowired
  private NewsletterServiceClient newsletterServiceClient;

  @Autowired
  private EmailService emailService;

  public User getUser(String authorizationHeader) {
    try {
      User user = userServiceClient.getUserByJwt(authorizationHeader);
      if (user != null) {
        return user;
      } else {
        throw new Exception("User not found for the given authorization header.");
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to retrieve user from user service", e);
    }
  }

  public User getUserById(Integer userId) {
    try {
      User user = userServiceClient.getUserById(userId);
      if (user != null) {
        return user;
      } else {
        throw new Exception("User not found for the given authorization header.");
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to retrieve user from user service", e);
    }
  }

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

  @Transactional
  public void subscribe(String authorizationHeader, Integer newsletterId) {
    User user = getUser(authorizationHeader);
    Newsletter newsletter = getNewsletter(newsletterId);

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

  public void sendNewsletterToSubscribedUsers(Integer newsletterId,
                                              String authorizationHeader) {
    List<Integer> subscribedUsers = getUsersSubscribedToNewsletter(newsletterId);
    Newsletter newsletter = getNewsletter(newsletterId);
    String subject = "Newsletter: " + newsletter.getTitle();
    String emailContent = "Dear Subscriber, \n\n" +
        "Here is the latest edition of the newsletter: " + newsletter.getDescription() + "\n\n" +
        "Best regards,\nNewsletter Team";

    for (Integer userId : subscribedUsers) {
      User user = getUserById(userId);
      emailService.sendNewsletterEmail(user.getEmail(), subject, emailContent);
    }
  }

  public List<UserSubscription> getAllSubscriptionsForUser(String authorizationHeader) {
    User user = getUser(authorizationHeader);
    return userSubscriptionRepository.findByUserId(user.getId());
  }

  @Transactional
  public void renewSubscription(String authorizationHeader, Long subscriptionId) {
    User user = getUser(authorizationHeader);
    UserSubscription subscription = userSubscriptionRepository.findById(subscriptionId)
        .orElseThrow(() -> new RuntimeException("Subscription not found"));

    if(!Objects.equals(subscription.getUserId(), user.getId())) {
      throw new RuntimeException("You are not authorized");
    }
    if (!subscription.isActive()) {
      throw new RuntimeException("Cannot renew an inactive subscription");
    }

    Newsletter newsletter = getNewsletter(subscription.getNewsletterId());
    LocalDateTime newEndDate = subscription.
        getSubscriptionEndDate().
        atStartOfDay().
        plus(newsletter.getSubscriptionPeriod());

    subscription.setSubscriptionEndDate(newEndDate.toLocalDate());
    userSubscriptionRepository.save(subscription);
  }
}
