package com.example.assignment.repository;

import com.example.assignment.models.UserSubscription;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserSubscriptionRepository extends CrudRepository<UserSubscription, Integer> {
  List<UserSubscription> findByUserId(Integer userId);
  Optional<UserSubscription> findById(Long id);
  Optional<UserSubscription> findByUserIdAndNewsletterId(Integer userId, Integer newsletterId);

  List<UserSubscription> findByNewsletterId(Integer newsletterId);
}
