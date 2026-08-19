package com.digicart.catalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LiquibaseUrlPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String dbUrl = environment.getProperty("DATABASE_URL");
        if (dbUrl == null || !dbUrl.contains("currentSchema=")) return;

        Matcher m = Pattern.compile("[?&]currentSchema=([^&]+)").matcher(dbUrl);
        String schema = m.find() ? m.group(1) : null;

        String cleanUrl = dbUrl.replaceAll("[&?]currentSchema=[^&]*", "").replaceAll("[?&]$", "");

        Map<String, Object> props = new HashMap<>();
        props.put("spring.datasource.url", cleanUrl);
        if (schema != null && !schema.isEmpty()) {
            props.put("spring.jpa.properties.hibernate.default_schema", schema);
        }
        environment.getPropertySources().addFirst(new MapPropertySource("liquibase-url-cleanup", props));
    }
}
