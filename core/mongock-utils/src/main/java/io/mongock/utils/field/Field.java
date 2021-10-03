package io.mongock.utils.field;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Set of changes to be added to the DB. Many changesets are included in one changelog.
 *
 * @since 27/07/2014
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Field {

  String value();

}
