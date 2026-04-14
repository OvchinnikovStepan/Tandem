package com.tandem.auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.core.env.Environment;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableScheduling
public class AuthServiceApplication {

	private final Environment environment;

	public AuthServiceApplication(Environment environment) {
		this.environment = environment;
}
	@PostConstruct
	public void init() {
		System.out.println("Redis host: " + environment.getProperty("spring.redis.host"));
		System.out.println("Redis port: " + environment.getProperty("spring.redis.port"));
		System.out.println("REDIS_HOST env: " + System.getenv("REDIS_HOST"));
		System.out.println("SPRING_REDIS_HOST env: " + System.getenv("SPRING_REDIS_HOST"));
	}
	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}
