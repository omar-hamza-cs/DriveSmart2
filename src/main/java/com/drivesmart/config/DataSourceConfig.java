package com.drivesmart.config;

import java.net.URI;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {
    @Bean
    @Primary
    public DataSource dataSource(Environment env) {
        String databaseUrl = env.getProperty("DATABASE_URL");
        String springUrl = env.getProperty("SPRING_DATASOURCE_URL");
        
        // Try SPRING_DATASOURCE_URL first
        if (springUrl != null && !springUrl.isBlank()) {
            return createDataSourceFromUrl(springUrl, env);
        }
        
        // Try DATABASE_URL second
        if (databaseUrl != null && !databaseUrl.isBlank()) {
            return createDataSourceFromUrl(databaseUrl, env);
        }
        
        // Try PG* variables
        return createDataSourceFromPGVariables(env);
    }
    
    private DataSource createDataSourceFromUrl(String url, Environment env) {
        HikariDataSource ds = new HikariDataSource();
        
        // Remove "postgres://" prefix if present
        String normalized = url.replace("postgres://", "postgresql://");
        
        // Add "jdbc:" prefix if missing
        if (!normalized.startsWith("jdbc:")) {
            normalized = "jdbc:" + normalized;
        }
        
        // Parse credentials from URL if they exist
        try {
            // Extract the part after jdbc:
            String urlWithoutJdbc = normalized.substring(5); // Remove "jdbc:"
            URI uri = URI.create(urlWithoutJdbc);
            
            String userInfo = uri.getUserInfo();
            if (userInfo != null) {
                // URL has credentials embedded (user:pass@host)
                String[] parts = userInfo.split(":", 2);
                String username = parts.length > 0 ? parts[0] : null;
                String password = parts.length > 1 ? parts[1] : null;
                
                ds.setJdbcUrl(normalized);
                ds.setUsername(username);
                ds.setPassword(password);
                return ds;
            } else {
                // URL doesn't have credentials, use separate variables
                ds.setJdbcUrl(normalized);
                ds.setUsername(env.getProperty("SPRING_DATASOURCE_USERNAME", 
                             env.getProperty("PGUSER", "postgres")));
                ds.setPassword(env.getProperty("SPRING_DATASOURCE_PASSWORD", 
                             env.getProperty("PGPASSWORD", "")));
                return ds;
            }
        } catch (Exception e) {
            // Fallback: use URL as-is
            ds.setJdbcUrl(normalized);
            return ds;
        }
    }
    
    private DataSource createDataSourceFromPGVariables(Environment env) {
        String host = env.getProperty("PGHOST");
        String port = env.getProperty("PGPORT", "5432");
        String db = env.getProperty("PGDATABASE");
        String username = env.getProperty("PGUSER", "postgres");
        String password = env.getProperty("PGPASSWORD", "");
        
        if (host != null && db != null) {
            String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + db;
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(jdbcUrl);
            ds.setUsername(username);
            ds.setPassword(password);
            return ds;
        }
        
        // Final fallback to application.properties
        String fallbackUrl = env.getProperty("spring.datasource.url");
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(fallbackUrl);
        ds.setUsername(env.getProperty("spring.datasource.username"));
        ds.setPassword(env.getProperty("spring.datasource.password"));
        return ds;
    }
}