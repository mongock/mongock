package io.mongock.runner.core.builder.roles;

import io.mongock.api.config.MongockConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public interface ChangeLogScanner<SELF extends ChangeLogScanner<SELF, CONFIG>, CONFIG extends MongockConfiguration>
    extends Configurable<SELF, CONFIG>, SelfInstanstiator<SELF> {


  /**
   * Sets the default migration author to be used when a changeLog doesn't provide author.
   * @param defaultMigrationAuthor the default author
   * @return builder for fluent interface
   */
  default SELF setDefaultMigrationAuthor(String defaultMigrationAuthor) {
    getConfig().setDefaultMigrationAuthor(defaultMigrationAuthor);
    return getInstance();
  }

  /**
   * Adds a list of packages to be scanned  to the list. Mongock allows multiple classes and packages
   * <b>Requires at least one package</b>
   *
   * @param migrationScanPackageList list of packages to be scanned
   * @return builder for fluent interface
   */
  default SELF addMigrationScanPackages(List<String> migrationScanPackageList) {
    if (migrationScanPackageList != null) {
      getConfig().getMigrationScanPackage().addAll(migrationScanPackageList);
    }
    return getInstance();
  }

  /**
   * Adds a package to be scanned  to the list. Mongock allows multiple classes and packages
   * <b>Requires at least one package</b>
   *
   * @param migrationScanPackage package to be scanned
   * @return builder for fluent interface
   */
  default SELF addMigrationScanPackage(String migrationScanPackage) {
    return addMigrationScanPackages(Collections.singletonList(migrationScanPackage));
  }

  /**
   * Adds a list of classes to be scanned  to the list. Mongock allows multiple classes and packages
   * <b>Requires at least one class</b>
   *
   * @param classes list of classes to be scanned
   * @return builder for fluent interface
   */
  default SELF addMigrationClasses(List<Class<?>> classes) {
    if (classes != null) {
      classes.stream().map(Class::getName).forEach(getConfig().getMigrationScanPackage()::add);
    }
    return getInstance();
  }

  /**
   * Adds a class to be scanned  to the list. Mongock allows multiple classes and packages
   * <b>Requires at least one package</b>
   *
   * @param clazz package to be scanned
   * @return builder for fluent interface
   */
  default SELF addMigrationClass(Class<?> clazz) {
    return addMigrationClasses(Collections.singletonList(clazz));
  }

  /**
   * Sets a function that will be used to instantiate ChangeLog classes.
   * If unset, Class.getConstructor().newInstance() will be used
   *
   * Deprecated: In future versions will be ignored. Internal 'instantiator' will be sued instead,
   * passing parameters from the context in the constructor(first found). In the current version(5),
   * only we used for changeLogs used on changeLogs based on annotations.
   *
   * @param changeLogInstantiator the function that will create an instance of a class
   * @return builder for fluent interface
   */
  @Deprecated
  SELF setChangeLogInstantiator(Function<Class<?>, Object> changeLogInstantiator);


  /**
   * DEPRECATIONS
   **/

  /**
   * Deprecated. Use addMigrationScanPackages instead
   */
  @Deprecated
  default SELF addChangeLogsScanPackages(List<String> migrationScanPackageList) {
    return addMigrationScanPackages(migrationScanPackageList);
  }

  /**
   * Deprecated. Use addChangeLogsScanPackage instead
   */
  @Deprecated
  default SELF addChangeLogsScanPackage(String migrationScanPackage) {
    return addMigrationScanPackage(migrationScanPackage);
  }

  /**
   * Deprecated. Use addChangeLogClasses instead
   */
  @Deprecated
  default SELF addChangeLogClasses(List<Class<?>> classes) {
    return addMigrationClasses(classes);
  }

  /**
   * Deprecated. Use addChangeLogClass instead
   */
  @Deprecated
  default SELF addChangeLogClass(Class<?> clazz) {
    return addMigrationClass(clazz);
  }

}
