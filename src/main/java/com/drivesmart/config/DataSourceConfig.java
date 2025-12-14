package com.drivesmart.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DataSourceConfig {
    @Bean
    @Primary
    public DataSource dataSource(Environment env) {
        String databaseUrl = env.getProperty("DATABASE_URL");
        String springUrl = env.getProperty("SPRING_DATASOURCE_URL");
        if (springUrl != null && !springUrl.isBlank()) {
            if (springUrl.startsWith("postgres://")) {
                databaseUrl = springUrl;
            } else {
                HikariDataSource ds = new HikariDataSource();
                ds.setJdbcUrl(springUrl);
                ds.setUsername(env.getProperty("SPRING_DATASOURCE_USERNAME"));
                ds.setPassword(env.getProperty("SPRING_DATASOURCE_PASSWORD"));
                return ds;
            }
        }
        if (databaseUrl != null && !databaseUrl.isBlank()) {
            String normalized = databaseUrl.replace("postgres://", "postgresql://");
            URI uri = URI.create(normalized);
            String userInfo = uri.getUserInfo();
            String username = null;
            String password = null;
            if (userInfo != null) {
                String[] parts = userInfo.split(":", 2);
                username = parts.length > 0 ? parts[0] : null;
                password = parts.length > 1 ? parts[1] : null;
            }
            String host = uri.getHost();
            int port = uri.getPort() > 0 ? uri.getPort() : 5432;
            String path = uri.getPath();
            String db = path != null && path.startsWith("/") ? path.substring(1) : path;
            String jdbc = "jdbc:postgresql://" + host + ":" + port + "/" + db;
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(jdbc);
            ds.setUsername(username);
            ds.setPassword(password);
            return ds;
        }
        String host = env.getProperty("PGHOST");
        String port = env.getProperty("PGPORT", "5432");
        String db = env.getProperty("PGDATABASE");
        String username = env.getProperty("PGUSER");
        String password = env.getProperty("PGPASSWORD");
        if (host != null && db != null && username != null) {
            String jdbc = "jdbc:postgresql://" + host + ":" + port + "/" + db;
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(jdbc);
            ds.setUsername(username);
            ds.setPassword(password);
            return ds;
        }
        String fallbackUrl = env.getProperty("spring.datasource.url");
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(fallbackUrl);
        ds.setUsername(env.getProperty("spring.datasource.username"));
        ds.setPassword(env.getProperty("spring.datasource.password"));
        return ds;
    }
}

