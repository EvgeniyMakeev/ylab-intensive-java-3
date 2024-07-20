package dev.makeev.coworking_service_app;

import dev.makeev.logging_time_starter.advice.annotations.EnableLoggingTime;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableLoggingTime
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}
