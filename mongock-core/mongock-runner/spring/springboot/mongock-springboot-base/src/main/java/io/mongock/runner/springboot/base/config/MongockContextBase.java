package io.mongock.runner.springboot.base.config;

import io.mongock.api.config.MongockConfiguration;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.runner.springboot.base.MongockApplicationRunner;
import io.mongock.runner.springboot.base.MongockInitializingBeanRunner;
import io.mongock.runner.springboot.base.builder.SpringApplicationBean;
import io.mongock.utils.Constants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;


public abstract class MongockContextBase<CONFIG extends MongockConfiguration> {

  @Bean
  @Profile(Constants.NON_CLI_PROFILE)
  @ConditionalOnExpression("'${mongock.runner-type:ApplicationRunner}'.toLowerCase().equals('applicationrunner')")
  public MongockApplicationRunner applicationRunner(ConnectionDriver connectionDriver,
                                                    CONFIG springConfiguration,
                                                    ApplicationContext springContext,
                                                    ApplicationEventPublisher applicationEventPublisher) {

    return getBuilder(connectionDriver, springConfiguration, springContext, applicationEventPublisher)
        .buildApplicationRunner();
  }

  @Bean
  @Profile(Constants.NON_CLI_PROFILE)
  @ConditionalOnExpression("'${mongock.runner-type:null}'.toLowerCase().equals('initializingbean')")
  public MongockInitializingBeanRunner initializingBeanRunner(ConnectionDriver connectionDriver,
                                                              CONFIG springConfiguration,
                                                              ApplicationContext springContext,
                                                              ApplicationEventPublisher applicationEventPublisher) {
    return getBuilder(connectionDriver, springConfiguration, springContext, applicationEventPublisher)
        .buildInitializingBeanRunner();
  }

  @SuppressWarnings("all")
  public abstract SpringApplicationBean getBuilder(ConnectionDriver connectionDriver,
												   CONFIG springConfiguration,
												   ApplicationContext springContext,
												   ApplicationEventPublisher applicationEventPublisher);
}


