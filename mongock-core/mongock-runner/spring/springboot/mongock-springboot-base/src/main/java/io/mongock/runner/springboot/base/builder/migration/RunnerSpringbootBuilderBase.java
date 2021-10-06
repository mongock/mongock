package io.mongock.runner.springboot.base.builder.migration;

import io.mongock.api.config.MongockConfiguration;
import io.mongock.driver.api.driver.ChangeSetDependency;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.runner.springboot.base.builder.SpringApplicationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

//TODO javadoc
public interface RunnerSpringbootBuilderBase<
    SELF extends RunnerSpringbootBuilderBase<SELF, CHANGE_ENTRY, CONFIG>,
    CHANGE_ENTRY extends ChangeEntry,
    CONFIG extends MongockConfiguration>
    extends
    SpringApplicationBean,
    RunnerBuilder<SELF, CHANGE_ENTRY, CONFIG> {

  //TODO javadoc
  SELF setSpringContext(ApplicationContext springContext);

  //TODO javadoc
  SELF setEventPublisher(ApplicationEventPublisher applicationEventPublisher);

  @Override
  default SELF addDependency(String name, Class<?> type, Object instance) {
    getDependencyManager().addDriverDependency(new ChangeSetDependency(name, type, instance));
    return getInstance();
  }
}
