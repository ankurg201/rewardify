package com.reward.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main entry point for the Reward Application.
 * <p>
 * This class initializes and runs the Spring Boot application.
 * </p>
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.reward.app")
public class RewardApplication {

	/**
	 * Main method to launch the Spring Boot application.
	 *
	 * @param args command-line arguments passed during application startup
	 */
	public static void main(String[] args) {
		SpringApplication.run(RewardApplication.class, args);
	}
}
