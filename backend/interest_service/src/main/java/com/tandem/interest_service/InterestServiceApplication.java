package com.tandem.interest_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.tandem.interest_service")
public class InterestServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(InterestServiceApplication.class, args);
	}
}