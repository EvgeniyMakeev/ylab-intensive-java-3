package dev.makeev.coworking_service_app.advice;

import dev.makeev.coworking_service_app.advice.annotations.LoggingToDb;
import dev.makeev.coworking_service_app.service.LogService;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.lang.reflect.Method;


@Configuration
@EnableAspectJAutoProxy
public class LoggingToDbAdvice implements AfterReturningAdvice {

    private final LogService logService;

    @Autowired
    public LoggingToDbAdvice(LogService logService) {
        this.logService = logService;
    }

    @Bean
    public DefaultPointcutAdvisor loggingToDbAdvisor() {
        AnnotationMatchingPointcut pointcut = new AnnotationMatchingPointcut(null, LoggingToDb.class);
        return new DefaultPointcutAdvisor(pointcut, this);
    }

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) {
        String loginArg = "No args";
        try {
            if (args.length > 0) {
                loginArg = args[0].toString();
            }
        } catch (Exception e) {
            System.err.println("Error processing method arguments: " + e.getMessage());
        }

        String methodName = method.getName();
        switch (method.getName()) {
            case "addUser" -> methodName = "Registered.";
            case "addBooking" -> methodName = "Add new booking " + args[1].toString();
            case "getAllBookingsForUser" -> methodName = "Looked at a bookings.";
            case "deleteBookingById" -> methodName = "Cancelled a booking with ID: " + args[1].toString();
        }

        logService.addLog(loginArg, methodName);
    }
}
