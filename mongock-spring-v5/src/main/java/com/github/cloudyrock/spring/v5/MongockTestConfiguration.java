package com.github.cloudyrock.spring.v5;

import io.changock.runner.spring.v5.config.test.ChangockTestContext;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Deprecated. Use @ChangockTestConfiguration instead
 *
 * @see io.changock.runner.spring.v5.config.test.ChangockTestConfiguration
 */
@Deprecated
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({ChangockTestContext.class})
public @interface MongockTestConfiguration {
}
