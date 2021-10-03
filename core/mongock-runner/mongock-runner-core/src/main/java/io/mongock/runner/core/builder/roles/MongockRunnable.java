package io.mongock.runner.core.builder.roles;

import io.mongock.api.config.MongockConfiguration;
import io.mongock.runner.core.executor.MongockRunner;
import io.mongock.runner.core.executor.operation.Operation;

public interface MongockRunnable<SELF extends MongockRunnable<SELF, CONFIG>, CONFIG extends MongockConfiguration>
    extends Configurable<SELF, CONFIG>, SelfInstanstiator<SELF> {

  SELF setExecutionId(String executionId);

  /**
   * Feature which enables/disables execution
   * <b>Optional</b> Default value true.
   *
   * @param enabled Migration process will run only if this option is set to true
   * @return builder for fluent interface
   */
  default SELF setEnabled(boolean enabled) {
    getConfig().setEnabled(enabled);
    return getInstance();
  }

  //todo javadoc
  MongockRunner buildRunner();

  MongockRunner buildRunner(Operation operation);
}
