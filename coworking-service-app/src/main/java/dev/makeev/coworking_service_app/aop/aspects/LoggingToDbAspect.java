package dev.makeev.coworking_service_app.aop.aspects;

import dev.makeev.coworking_service_app.dao.implementation.LogDaoInBd;
import dev.makeev.coworking_service_app.service.LogService;
import dev.makeev.coworking_service_app.util.implementation.ConnectionManagerImpl;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class LoggingToDbAspect {

    private final LogService logService = new LogService(new LogDaoInBd(new ConnectionManagerImpl()));

    @Pointcut("@annotation(dev.makeev.coworking_service_app.aop.annotations.LoggingToDb)")
    public void annotatedByLoggingToDb() {
    }

    @After("annotatedByLoggingToDb()")
    public void loggingToDB(JoinPoint joinPoint) {
        String args = "No args";
        try {
            Object[] methodArgs = joinPoint.getArgs();
            if (methodArgs.length > 0) {
                args = methodArgs[0].toString();
            }
        } catch (Exception e) {
            System.err.println("Error processing method arguments: " + e.getMessage());
        }

        String message = joinPoint.getSignature().getName();
        logService.addLog(args, message);
    }
}

