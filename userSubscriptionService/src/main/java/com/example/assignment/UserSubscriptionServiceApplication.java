package com.example.assignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.example.assignment.services")
@EnableCaching
public class UserSubscriptionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserSubscriptionServiceApplication.class, args);
	}
}
