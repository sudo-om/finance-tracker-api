package com.financetracker.finance_tracker_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.URI;

@SpringBootApplication
@EnableScheduling
public class FinanceTrackerApiApplication {

	public static void main(String[] args) {
		String dbUrl = System.getenv("SPRING_DATASOURCE_URL");
		if (dbUrl == null || dbUrl.isEmpty()) {
			dbUrl = System.getenv("DATABASE_URL");
		}

		if (dbUrl != null && !dbUrl.isEmpty()) {
			try {
				if (dbUrl.startsWith("postgres://") || dbUrl.startsWith("postgresql://")) {
					URI uri = new URI(dbUrl);
					String host = uri.getHost();
					int port = uri.getPort() == -1 ? 5432 : uri.getPort();
					String path = uri.getPath();

					String userInfo = uri.getUserInfo();
					if (userInfo != null && userInfo.contains(":")) {
						String[] parts = userInfo.split(":", 2);
						System.setProperty("spring.datasource.username", parts[0]);
						System.setProperty("spring.datasource.password", parts[1]);
					}

					String cleanJdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
					System.setProperty("spring.datasource.url", cleanJdbcUrl);
				} else if (dbUrl.startsWith("jdbc:postgresql://") && dbUrl.contains("@")) {
					String raw = dbUrl.substring("jdbc:postgresql://".length());
					URI uri = new URI("http://" + raw);
					String host = uri.getHost();
					int port = uri.getPort() == -1 ? 5432 : uri.getPort();
					String path = uri.getPath();

					String userInfo = uri.getUserInfo();
					if (userInfo != null && userInfo.contains(":")) {
						String[] parts = userInfo.split(":", 2);
						System.setProperty("spring.datasource.username", parts[0]);
						System.setProperty("spring.datasource.password", parts[1]);
					}

					String cleanJdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
					System.setProperty("spring.datasource.url", cleanJdbcUrl);
				}
			} catch (Exception e) {
				System.err.println("Failed to parse database URI: " + e.getMessage());
			}
		}

		SpringApplication.run(FinanceTrackerApiApplication.class, args);
	}
}
