package com.reward.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.reward.app")
public class RewardApplication {

	public static void main(String[] args) {
		SpringApplication.run(RewardApplication.class, args);
	}
}
