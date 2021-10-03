package io.mongock.runner.core.builder.roles;

import io.mongock.driver.api.driver.ChangeSetDependency;
import io.mongock.runner.core.executor.dependency.DependencyManager;

public interface DependencyInjectable<SELF extends DependencyInjectable<SELF>>
    extends SelfInstanstiator<SELF> {


  /**
   * Manually adds a dependency to be used in the  changeUnits, which can be retrieved by its own type
   *
   * @param instance dependency
   * @return builder for fluent interface
   */
  default SELF addDependency(Object instance) {
    return addDependency(instance.getClass(), instance);
  }

  /**
   * Manually adds a dependency to be used in the  changeUnits, which can be retrieved by a name
   *
   * @param name     name for which it should be searched by
   * @param instance dependency
   * @return builder for fluent interface
   */
  default SELF addDependency(String name, Object instance) {
    return addDependency(name, instance.getClass(), instance);
  }

  /**
   * Manually adds a dependency to be used in the  changeUnits, which can be retrieved by a type
   *
   * @param type     type for which it should be searched by
   * @param instance dependency
   * @return builder for fluent interface
   */
  default SELF addDependency(Class<?> type, Object instance) {
    return addDependency(ChangeSetDependency.DEFAULT_NAME, type, instance);
  }

  /**
   * Manually adds a dependency to be used in the  changeUnits, which can be retrieved by a type or name
   *
   * @param name     name for which it should be searched by
   * @param type     type for which it should be searched by
   * @param instance dependency
   * @return builder for fluent interface
   */
  default SELF addDependency(String name, Class<?> type, Object instance) {
    getDependencyManager().addStandardDependency(new ChangeSetDependency(name, type, instance));
    return getInstance();
  }

  DependencyManager getDependencyManager();
}
