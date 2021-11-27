package io.mongock.runner.springboot.config;

import io.mongock.api.config.MongockConfiguration;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.runner.springboot.MongockSpringboot;
import io.mongock.runner.springboot.RunnerSpringbootBuilder;
import io.mongock.runner.springboot.base.config.MongockContextBase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;

@ConditionalOnExpression("${mongock.enabled:true}")
public class MongockContext extends MongockContextBase<MongockConfiguration> {


  @Bean
  public RunnerSpringbootBuilder getBuilder(ConnectionDriver connectionDriver,
                                            MongockConfiguration springConfiguration,
                                            ApplicationContext springContext,
                                            ApplicationEventPublisher applicationEventPublisher) {
    return MongockSpringboot.builder()
        .setDriver(connectionDriver)
        .setConfig(springConfiguration)
        .setSpringContext(springContext)
        .setEventPublisher(applicationEventPublisher);
  }
}


