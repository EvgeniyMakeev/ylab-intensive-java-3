package dev.makeev.coworking_service_app.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
//@EnableAspectJAutoProxy
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = "dev.makeev.coworking_service_app")
public class AppConfig {
}
