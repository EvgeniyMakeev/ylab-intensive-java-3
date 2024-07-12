package dev.makeev.coworking_service_app.advice;

import dev.makeev.coworking_service_app.advice.annotations.LoggingTime;
import dev.makeev.coworking_service_app.out.Output;
import dev.makeev.coworking_service_app.out.implementation.ConsoleOutput;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class LoggingTimeAdvice implements MethodInterceptor {

    private final Output<String> output = new ConsoleOutput();
    private final ThreadLocal<Integer> callDepth = ThreadLocal.withInitial(() -> 0);

    @Bean
    public DefaultPointcutAdvisor loggingTimeAdvisor() {
        AnnotationMatchingPointcut pointcut = new AnnotationMatchingPointcut(null, LoggingTime.class);
        return new DefaultPointcutAdvisor(pointcut, this);
    }

    @Override
    public Object invoke(MethodInvocation invocation) {
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
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            callDepth.set(callDepth.get() - 1);
        }
    }
}
