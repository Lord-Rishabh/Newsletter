package com.example.assignment.services;

import com.example.assignment.models.Newsletter;
import com.example.assignment.models.User;
import com.example.assignment.services.FeignClient.NewsletterService;
import com.example.assignment.services.FeignClient.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

  @Autowired
  private JavaMailSender mailSender;

  @Autowired
  private UserSubscriptionService userSubscriptionService;

  @Autowired
  private UserService userService;

  @Autowired
  private NewsletterService newsletterService;

  public void sendNewsletterEmail(String to, String subject, String text) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject(subject);
    message.setText(text);
    mailSender.send(message);
  }

  public void sendNewsletterToSubscribedUsers(Integer newsletterId,
                                              String authorizationHeader) {
    List<Integer> subscribedUsers = userSubscriptionService.getUsersSubscribedToNewsletter(newsletterId);
    Newsletter newsletter = newsletterService.getNewsletter(newsletterId);

    if(!userService.checkAdminRole(authorizationHeader)) {
      throw new RuntimeException("You are not authorized to make this request.");
    };

    String subject = "Newsletter: " + newsletter.getTitle();
    String emailContent = "Dear Subscriber, \n\n" +
        "Here is the latest edition of the newsletter: " + newsletter.getDescription() + "\n\n" +
        "Best regards,\nNewsletter Team";

    for (Integer userId : subscribedUsers) {
      User user = userService.getUserById(userId);
      sendNewsletterEmail(user.getEmail(), subject, emailContent);
    }
  }

}
