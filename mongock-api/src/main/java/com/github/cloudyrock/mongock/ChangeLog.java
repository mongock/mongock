package com.github.cloudyrock.mongock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class containing particular changeSets (@{@link ChangeSet})
 * Deprecated, please use @ChangeLog from Changock
 *
 * @see io.changock.migration.api.annotations.ChangeLog
 * @since 27/07/2014
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
public @interface ChangeLog {
  /**
   * Sequence that provide an order for changelog classes.
   * If not set, then canonical name of the class is taken and sorted alphabetically, ascending.
   *
   * @return order
   */
  String order() default "";

}
