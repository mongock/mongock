package com.github.cloudyrock.spring.v5;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(MongockCoreContextSelector.class)
@ConditionalOnProperty(prefix = "mongock", name = "enabled", matchIfMissing = true, havingValue = "true")
public class MongockContext {

  @Bean
  @ConditionalOnProperty(prefix = "mongock", value = "runner-type", matchIfMissing = true, havingValue = "ApplicationRunner")
  public MongockSpring5.MongockApplicationRunner mongockApplicationRunner(MongockSpring5.Builder mongockBuilder) {
    return mongockBuilder.buildApplicationRunner();
  }

  @Bean
  @ConditionalOnProperty(prefix = "mongock", value = "runner-type", havingValue = "InitializingBean")
  public MongockSpring5.MongockInitializingBeanRunner mongockInitializingBeanRunner(MongockSpring5.Builder mongockBuilder) {
    return mongockBuilder.buildInitializingBeanRunner();
  }
}
