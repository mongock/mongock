package com.github.cloudyrock.spring.v5;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(MongockCoreContextSelector.class)
public class MongockContext {

  @Bean
  @ConditionalOnProperty(value = "mongock.runner-type", matchIfMissing = true, havingValue = "ApplicationRunner")
  public MongockSpring5.MongockApplicationRunner mongockApplicationRunner(MongockSpring5.Builder mongockBuilder) {
    return mongockBuilder.buildApplicationRunner();
  }

  @Bean
  @ConditionalOnProperty(value = "mongock.runner-type", havingValue = "InitializingBean")
  public MongockSpring5.MongockInitializingBeanRunner mongockInitializingBeanRunner(MongockSpring5.Builder mongockBuilder) {
    return mongockBuilder.buildInitializingBeanRunner();
  }
}
