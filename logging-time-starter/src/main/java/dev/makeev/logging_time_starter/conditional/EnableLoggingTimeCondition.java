package dev.makeev.logging_time_starter.conditional;


import dev.makeev.logging_time_starter.advice.annotations.EnableLoggingTime;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Objects;

public class EnableLoggingTimeCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return !Objects.requireNonNull(context.getBeanFactory()).getBeansWithAnnotation(EnableLoggingTime.class).isEmpty();
    }
}