package dev.makeev.coworking_service_app.aop.aspects;

import dev.makeev.coworking_service_app.out.Output;
import dev.makeev.coworking_service_app.out.implementation.ConsoleOutput;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class LoggingTimeAspect {

    private final Output<String> output = new ConsoleOutput();

    @Pointcut("@annotation(dev.makeev.coworking_service_app.aop.annotations.LoggingTime)")
    public void annotatedByLoggingTime() {
    }

    @Around("annotatedByLoggingTime()")
    public Object loggingToConsole(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("Calling method " + proceedingJoinPoint.getSignature());
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long endTime = System.currentTimeMillis();
        output.output("Execution of method " + proceedingJoinPoint.getSignature() +
                " finished. Execution time is " + (endTime - startTime) + " ms");
        return result;
    }

}
