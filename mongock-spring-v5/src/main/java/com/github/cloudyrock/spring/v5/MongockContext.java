package com.github.cloudyrock.spring.v5;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.runner.spring.v5.SpringApplicationRunner;
import io.changock.runner.spring.v5.SpringInitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

//@Configuration
@Import(MongockCoreContextSelector.class)
@ConditionalOnProperty(prefix = "mongock", name = "enabled", matchIfMissing = true, havingValue = "true")
public class MongockContext {

  @Bean
  @ConditionalOnProperty(value = "mongock.runner-type", matchIfMissing = true, havingValue = "ApplicationRunner")
  public SpringApplicationRunner mongockApplicationRunner(ConnectionDriver mongockConnectionDriver,
                                                          MongockConfiguration mongockConfiguration,
                                                          ApplicationContext springContext,
                                                          ApplicationEventPublisher applicationEventPublisher) {
    return mongockBuilder(mongockConnectionDriver, mongockConfiguration, springContext, applicationEventPublisher)
        .buildApplicationRunner();
  }

  @Bean
  @ConditionalOnProperty(value = "mongock.runner-type", havingValue = "InitializingBean")
  public SpringInitializingBean mongockInitializingBeanRunner(ConnectionDriver mongockConnectionDriver,
                                                              MongockConfiguration mongockConfiguration,
                                                              ApplicationContext springContext,
                                                              ApplicationEventPublisher applicationEventPublisher) {
    return mongockBuilder(mongockConnectionDriver, mongockConfiguration, springContext, applicationEventPublisher)
        .buildInitializingBeanRunner();
  }


  private MongockSpring5.Builder mongockBuilder(ConnectionDriver mongockConnectionDriver,
                                               MongockConfiguration mongockConfiguration,
                                               ApplicationContext springContext,
                                               ApplicationEventPublisher applicationEventPublisher) {
    return MongockSpring5.builder()
        .setDriver(mongockConnectionDriver)
        .setConfig(mongockConfiguration)
        .setSpringContext(springContext)
        .setEventPublisher(applicationEventPublisher);
  }
}


