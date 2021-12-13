package io.mongock.driver.api.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Internal use only. This is subject to be changed or removed without representing a breaking change
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SystemChange {

  boolean updatesSystemTable() default false;
}
