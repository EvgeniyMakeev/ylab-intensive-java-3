package dev.makeev.coworking_service_app.aop.aspects;

import dev.makeev.coworking_service_app.dao.implementation.LogDaoInBd;
import dev.makeev.coworking_service_app.service.LogService;
import dev.makeev.coworking_service_app.util.implementation.ConnectionManagerImpl;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Arrays;

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
            args = Arrays.toString(joinPoint.getArgs());
        } finally {
            String message = joinPoint.getSignature().getName();
            logService.addLog(args, message);
        }
    }

}
