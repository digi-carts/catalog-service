package com.digicart.catalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class LiquibaseUrlPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String dbUrl = environment.getProperty("DATABASE_URL");
        if (dbUrl == null || !dbUrl.contains("currentSchema=")) return;

        String liquibaseUrl = dbUrl.replaceAll("[&?]currentSchema=[^&]*", "").replaceAll("[&?]$", "");
        Map<String, Object> props = new HashMap<>();
        props.put("spring.liquibase.url", liquibaseUrl);
        environment.getPropertySources().addFirst(new MapPropertySource("liquibase-url-cleanup", props));
    }
}
