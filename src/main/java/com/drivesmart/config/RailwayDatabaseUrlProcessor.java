package com.drivesmart.config;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

public class RailwayDatabaseUrlProcessor implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, org.springframework.boot.SpringApplication application) {
        String raw = System.getenv("DATABASE_URL");
        if (raw == null || raw.isEmpty()) return;
        String normalized = raw.startsWith("postgres://") ? raw.replaceFirst("postgres://", "postgresql://") : raw;
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
        int port = uri.getPort() == -1 ? 5432 : uri.getPort();
        String path = uri.getPath();
        String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + (path == null ? "" : path);
        Map<String, Object> props = new HashMap<>();
        props.put("spring.datasource.url", jdbcUrl);
        if (username != null) props.put("spring.datasource.username", username);
        if (password != null) props.put("spring.datasource.password", password);
        props.put("spring.datasource.driver-class-name", "org.postgresql.Driver");
        environment.getPropertySources().addFirst(new MapPropertySource("railwayDatabase", props));
    }
}
