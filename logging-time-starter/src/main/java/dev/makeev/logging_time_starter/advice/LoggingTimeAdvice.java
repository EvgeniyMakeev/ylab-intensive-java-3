package dev.makeev.logging_time_starter.advice;

import dev.makeev.logging_time_starter.conditional.ConditionalOnEnableLoggingTime;
import dev.makeev.logging_time_starter.advice.annotations.LoggingTime;
import dev.makeev.logging_time_starter.out.Output;
import dev.makeev.logging_time_starter.out.implementation.ConsoleOutput;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Advice for logging method execution time.
 * This interceptor is triggered by methods annotated with {@link LoggingTime}.
 */
@Configuration
@ConditionalOnEnableLoggingTime
public class LoggingTimeAdvice implements MethodInterceptor {

    private final Output<String> output = new ConsoleOutput();
    private final ThreadLocal<Integer> callDepth = ThreadLocal.withInitial(() -> 0);

    /**
     * Configures a pointcut advisor to intercept methods annotated with {@link LoggingTime}.
     * @return DefaultPointcutAdvisor configured with the logging time advice.
     */
    @Bean
    public DefaultPointcutAdvisor loggingTimeAdvisor() {
        AnnotationMatchingPointcut pointcut = new AnnotationMatchingPointcut(null, LoggingTime.class);
        return new DefaultPointcutAdvisor(pointcut, this);
    }

    /**
     * Intercepts method execution to log the time taken for execution.
     * @param invocation MethodInvocation object containing details about the method being invoked.
     * @return The result of the method invocation.
     * @throws Throwable If any error occurs during method invocation.
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        callDepth.set(callDepth.get() + 1);
        try {
            if (callDepth.get() == 1) {
                long startTime = System.currentTimeMillis();
                output.output("Calling method " + invocation.getMethod().getName());
                Object result = invocation.proceed();
                long endTime = System.currentTimeMillis();
                output.output("Execution of method " + invocation.getMethod().getName() +
                        " finished. Execution time is " + (endTime - startTime) + " ms");
                return result;
            } else {
                return invocation.proceed();
            }
        } finally {
            callDepth.set(callDepth.get() - 1);
        }
    }
}
