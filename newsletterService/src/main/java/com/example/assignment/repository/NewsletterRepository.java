package com.example.assignment.repository;

import com.example.assignment.models.Newsletter;
import org.springframework.data.repository.CrudRepository;

public interface NewsletterRepository extends CrudRepository<Newsletter, Integer> {
}
