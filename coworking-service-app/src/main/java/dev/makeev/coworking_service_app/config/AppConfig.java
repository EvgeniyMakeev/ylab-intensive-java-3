package dev.makeev.coworking_service_app.config;

import dev.makeev.coworking_service_app.util.YamlPropertySourceFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:properties.yml", factory = YamlPropertySourceFactory.class)
@ComponentScan(basePackages = "dev.makeev.coworking_service_app")
public class AppConfig {
}
