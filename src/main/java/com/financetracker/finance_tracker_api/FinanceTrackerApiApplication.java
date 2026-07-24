package com.financetracker.finance_tracker_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FinanceTrackerApiApplication {

	public static void main(String[] args) {
		String dbUrl = System.getenv("SPRING_DATASOURCE_URL");
		if (dbUrl == null || dbUrl.isEmpty()) {
			dbUrl = System.getenv("DATABASE_URL");
		}
		if (dbUrl != null && !dbUrl.isEmpty()) {
			if (dbUrl.startsWith("postgres://")) {
				dbUrl = dbUrl.replace("postgres://", "jdbc:postgresql://");
			} else if (dbUrl.startsWith("postgresql://")) {
				dbUrl = dbUrl.replace("postgresql://", "jdbc:postgresql://");
			}
			System.setProperty("spring.datasource.url", dbUrl);
		}

		SpringApplication.run(FinanceTrackerApiApplication.class, args);
	}
}
