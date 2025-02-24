package com.reward.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
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
public class RewardApplication implements CommandLineRunner {

	@Autowired
	private JsonDataLoader jsonDataLoader;

	/**
	 * Main method to launch the Spring Boot application.
	 *
	 * @param args command-line arguments passed during application startup
	 */
	public static void main(String[] args) {
		SpringApplication.run(RewardApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		jsonDataLoader.loadJsonData(); // Load JSON on startup
	}
}
