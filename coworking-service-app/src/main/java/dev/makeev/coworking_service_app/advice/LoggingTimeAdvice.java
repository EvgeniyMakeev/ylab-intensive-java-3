package dev.makeev.coworking_service_app.advice;

import dev.makeev.coworking_service_app.advice.annotations.LoggingTime;
import dev.makeev.coworking_service_app.out.Output;
import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Advice for logging method execution time.
 * This interceptor is triggered by methods annotated with {@link LoggingTime}.
 */
@Configuration
@EnableAspectJAutoProxy
@RequiredArgsConstructor
public class LoggingTimeAdvice implements MethodInterceptor {

    private final Output<String> output;
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
