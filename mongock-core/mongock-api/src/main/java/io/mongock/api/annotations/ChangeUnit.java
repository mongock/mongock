package io.mongock.api.annotations;

import com.github.cloudyrock.mongock.ChangeSet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation replaces the old annotation @ChangeLog(and changeSet).
 * 
 * The main difference is that classes annotated with @ChangeUnit can only have one changeSet method, annotated with @Execution and optionally
 * another changeSet annotated with @BeforeExecution, which is run before the actual change is executed, this means, for example, that the method
 * annotated with @BeforeExecution will be out of the native transaction linked to the changeLog, however, Mongock will try to revert the changes applied
 * by the method @BeforeExecution by executing the method annotated with @RollbackBeforeExecution
 *
 * The concept is basically the same, a class that wraps the logic of the migration
 * 
 * Classes annotated with @ChangeUnit must have the following:
 * - One(and only one) method annotated with @Execution(mandatory)
 * - One(and only one) method annotated with @RollbackExecution(mandatory)
 * - At most, one method annotated with @BeforeExecution(optional)
 * - If contains a method annotated with @BeforeExecution, one(and only one) method annotated with @RollbackBeforeExecution(mandatory if @BeforeExecution present)
 *
 * Please follow one of the recommended approaches depending on your use case:
 *  - For existing changeLogs/changeSets created prior version 5: leave them untouched (use with the deprecated annotation)
 *
 *  - For new changeLogs/changeSets created  from version 5: Annotated you class migration class with the annotation @ChangeUnit
 *
 * @see Execution
 * @see BeforeExecution
 * @see RollbackExecution
 * @see RollbackBeforeExecution
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChangeUnit {

  /**
   * Change unit's id. It will be used to identify both change entries, the one linked to @Execution and @BeforeExecution(this one with the suffix `_before`)
   * Equivalent to field `id` in ChangeSet annotation
   *
   * @see ChangeSet
   * @return Change unit's id
   */
  String id();

  /**
   * Equivalent to field `order` in ChangeSet annotation and ChangeLog,
   * as now there is only one "changeSet", annotated with @Execution
   *
   * @see ChangeSet
   * @return ChangeSet's author
   */
  String order();

  /**
   * Equivalent to field `author` in ChangeSet annotation
   *
   *
   * @see ChangeSet
   * @return ChangeSet's author
   */
  String author() default "";

  /**
   * Equivalent to field `failFast` in ChangeSet annotation
   *
   * @see ChangeSet
   * @return ChangeSet if the ChangeLog is fail fast
   */
  boolean failFast() default true;

  /**
   * Equivalent to field `runAlways` in ChangeSet annotation
   *
   * @see ChangeSet
   * @return ChangeSet if the ChangeLog is runAlways
   */
  boolean runAlways() default false;

  /**
   * Equivalent to field `systemVersion` in ChangeSet annotation
   *
   * @see ChangeSet
   * @return ChangeSet if the ChangeLog is runAlways
   */
  String systemVersion()default "0";

}
