package io.mongock.api.annotations;

import com.github.cloudyrock.mongock.ChangeSet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation replaces the old annotation @ChangeLog(and changeSet).
 * <p>
 * The main difference is that classes annotated with @ChangeUnit can only have one changeSet method, annotated with @Execution and optionally
 * another changeSet annotated with @BeforeExecution, which is run before the actual change is executed, this means, for example, that the method
 * annotated with @BeforeExecution will be out of the native transaction linked to the changeLog, however, Mongock will try to revert the changes applied
 * by the method @BeforeExecution by executing the method annotated with @RollbackBeforeExecution
 * <p>
 * The concept is basically the same, a class that wraps the logic of the migration
 * <p>
 * Classes annotated with @ChangeUnit must have the following:
 * - One(and only one) one valid constructor annotated with @ChangeUnitConstructor(mandatory if more than one constructor exist)
 * - One(and only one) method annotated with @Execution(mandatory)
 * - One(and only one) method annotated with @RollbackExecution(mandatory)
 * - At most, one method annotated with @BeforeExecution(optional)
 * - If contains a method annotated with @BeforeExecution, one(and only one) method annotated with @RollbackBeforeExecution(mandatory if @BeforeExecution present)
 * <p>
 * Please follow one of the recommended approaches depending on your use case:
 * - For existing changeLogs/changeSets created prior version 5: leave them untouched (use with the deprecated annotation)
 * <p>
 * - For new changeLogs/changeSets created  from version 5: Annotated you class migration class with the annotation @ChangeUnit
 *
 * @see ChangeUnitConstructor
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
   * @return Change unit's id
   * @see ChangeSet
   */
  String id();

  /**
   * Equivalent to field `order` in ChangeSet annotation and ChangeLog,
   * as now there is only one "changeSet", annotated with @Execution
   *
   * @return ChangeSet's author
   * @see ChangeSet
   */
  String order();

  /**
   * Equivalent to field `author` in ChangeSet annotation
   *
   * @return ChangeSet's author
   * @see ChangeSet
   */
  String author() default "";

  /**
   * Equivalent to field `failFast` in ChangeSet annotation
   *
   * @return ChangeSet if the ChangeLog is fail fast
   * @see ChangeSet
   */
  boolean failFast() default true;

  /**
   * Equivalent to field `runAlways` in ChangeSet annotation
   *
   * @return ChangeSet if the ChangeLog is runAlways
   * @see ChangeSet
   */
  boolean runAlways() default false;

  /**
   * Equivalent to field `systemVersion` in ChangeSet annotation
   *
   * @return ChangeSet if the ChangeLog is runAlways
   * @see ChangeSet
   */
  String systemVersion() default "0";

  /**
   * If true, Mongock will try to run the changeUnit in a native transaction, if possible.
   *
   * @return If he changeUnit should be run in a native transaction.
   */
  boolean transactional() default true;

}
