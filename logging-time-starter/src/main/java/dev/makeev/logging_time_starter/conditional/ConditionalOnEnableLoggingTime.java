package dev.makeev.logging_time_starter.conditional;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Condition for enabling logging time functionality if {@link dev.makeev.logging_time_starter.advice.annotations.EnableLoggingTime} is present.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Conditional(EnableLoggingTimeCondition.class)
public @interface ConditionalOnEnableLoggingTime {
}