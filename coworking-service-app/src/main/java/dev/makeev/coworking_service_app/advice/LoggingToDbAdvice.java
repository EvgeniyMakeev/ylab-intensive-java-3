package dev.makeev.coworking_service_app.advice;

import dev.makeev.coworking_service_app.advice.annotations.LoggingToDb;
import dev.makeev.coworking_service_app.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.lang.reflect.Method;

/**
 * Advice for logging method actions to the database.
 * This interceptor is triggered by methods annotated with {@link LoggingToDb}.
 */
@Configuration
@EnableAspectJAutoProxy
@RequiredArgsConstructor
public class LoggingToDbAdvice implements AfterReturningAdvice {

    private final LogService logService;

    /**
     * Configures a pointcut advisor to intercept methods annotated with {@link LoggingToDb}.
     * @return DefaultPointcutAdvisor configured with the logging to database advice.
     */
    @Bean
    public DefaultPointcutAdvisor loggingToDbAdvisor() {
        AnnotationMatchingPointcut pointcut = new AnnotationMatchingPointcut(null, LoggingToDb.class);
        return new DefaultPointcutAdvisor(pointcut, this);
    }

    /**
     * Intercepts method execution after returning and logs the action to the database.
     * @param returnValue The value returned by the method, if any.
     * @param method The method that was called.
     * @param args The arguments passed to the method.
     * @param target The target object on which the method was called.
     */
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
            case "checkCredentials" -> methodName = "Login in.";
            case "logOut" -> methodName = "Login out.";
            case "addBooking" -> methodName = "Add new booking " + args[1].toString();
            case "getAllBookingsForUser" -> methodName = "Looked at a bookings.";
            case "deleteBookingById" -> methodName = "Cancelled a booking with ID: " + args[1].toString();
        }

        logService.addLog(loginArg, methodName);
    }
}
