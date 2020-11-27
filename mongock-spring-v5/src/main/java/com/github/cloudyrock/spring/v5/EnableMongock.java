package com.github.cloudyrock.spring.v5;

import io.changock.runner.spring.v5.config.ChangockContext;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Deprecated. Use @EnableChangock instead
 *
 * @see io.changock.runner.spring.v5.config.EnableChangock
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
@Import({ChangockContext.class})
public @interface EnableMongock {
}
