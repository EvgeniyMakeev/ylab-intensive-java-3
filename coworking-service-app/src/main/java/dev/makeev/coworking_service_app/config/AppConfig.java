package dev.makeev.coworking_service_app.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Spring application context.
 * Specifies component scanning base package to include all classes in `dev.makeev.coworking_service_app` package.
 */
@Configuration
@ComponentScan(basePackages = "dev.makeev.coworking_service_app")
public class AppConfig {
}
