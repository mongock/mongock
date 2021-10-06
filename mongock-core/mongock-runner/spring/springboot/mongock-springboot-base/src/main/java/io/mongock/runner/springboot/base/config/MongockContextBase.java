package io.mongock.runner.springboot.base.config;

import io.mongock.api.config.MongockConfiguration;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.runner.springboot.base.MongockApplicationRunner;
import io.mongock.runner.springboot.base.MongockInitializingBeanRunner;
import io.mongock.runner.springboot.base.builder.SpringApplicationBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;


public abstract class MongockContextBase<CHANGE_ENTRY extends ChangeEntry, CONFIG extends MongockConfiguration> {

  @Bean
  @ConditionalOnExpression("'${mongock.runner-type:ApplicationRunner}'.toLowerCase().equals('applicationrunner')")
  public MongockApplicationRunner applicationRunner(ConnectionDriver<CHANGE_ENTRY> connectionDriver,
                                                    CONFIG springConfiguration,
                                                    ApplicationContext springContext,
                                                    ApplicationEventPublisher applicationEventPublisher) {
    return getBuilder(connectionDriver, springConfiguration, springContext, applicationEventPublisher)
        .buildApplicationRunner();
  }

  @Bean
  @ConditionalOnExpression("'${mongock.runner-type:null}'.toLowerCase().equals('initializingbean')")
  public MongockInitializingBeanRunner initializingBeanRunner(ConnectionDriver<CHANGE_ENTRY> connectionDriver,
                                                              CONFIG springConfiguration,
                                                              ApplicationContext springContext,
                                                              ApplicationEventPublisher applicationEventPublisher) {
    return getBuilder(connectionDriver, springConfiguration, springContext, applicationEventPublisher)
        .buildInitializingBeanRunner();
  }

  @SuppressWarnings("all")
  public abstract SpringApplicationBean getBuilder(ConnectionDriver<CHANGE_ENTRY> connectionDriver,
												   CONFIG springConfiguration,
												   ApplicationContext springContext,
												   ApplicationEventPublisher applicationEventPublisher);
}


