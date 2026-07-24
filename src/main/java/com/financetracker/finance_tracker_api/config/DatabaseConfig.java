package com.financetracker.finance_tracker_api.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("!test")
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String rawUrl;

    @Value("${spring.datasource.username:postgres}")
    private String username;

    @Value("${spring.datasource.password:12345678}")
    private String password;

    @Bean
    public DataSource dataSource() {
        String formattedUrl = rawUrl;
        if (formattedUrl != null) {
            if (formattedUrl.startsWith("postgres://")) {
                formattedUrl = formattedUrl.replace("postgres://", "jdbc:postgresql://");
            } else if (formattedUrl.startsWith("postgresql://")) {
                formattedUrl = formattedUrl.replace("postgresql://", "jdbc:postgresql://");
            }
        }

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(formattedUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("org.postgresql.Driver");
        return dataSource;
    }
}
