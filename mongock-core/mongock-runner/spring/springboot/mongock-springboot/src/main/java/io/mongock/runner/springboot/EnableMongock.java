package io.mongock.runner.springboot;

import io.mongock.runner.springboot.config.MongockContext;
import io.mongock.runner.springboot.config.MongockSpringConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EnableConfigurationProperties
@Import({MongockContext.class, MongockSpringConfiguration.class})
public @interface EnableMongock {
}
