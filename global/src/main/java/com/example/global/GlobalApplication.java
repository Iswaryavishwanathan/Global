package com.example.global;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.example.global")
@EnableScheduling
// @ComponentScan(basePackages = "com.example.global")
// @EnableJpaRepositories(basePackages = "com.example.global.Repository.primary")
// @EntityScan(basePackages = "com.example.global.Entity")
public class GlobalApplication {

	public static void main(String[] args) {
		SpringApplication.run(GlobalApplication.class, args);
	}

}
