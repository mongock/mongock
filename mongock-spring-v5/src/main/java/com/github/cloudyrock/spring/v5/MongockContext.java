package com.github.cloudyrock.spring.v5;

import io.changock.runner.spring.v5.SpringApplicationRunner;
import io.changock.runner.spring.v5.SpringInitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

//@Configuration
@Import(MongockCoreContextSelector.class)
@ConditionalOnProperty(prefix = "mongock", name = "enabled", matchIfMissing = true, havingValue = "true")
public class MongockContext {

  @Bean
  @ConditionalOnProperty(value = "mongock.runner-type", matchIfMissing = true, havingValue = "ApplicationRunner")
  public SpringApplicationRunner mongockApplicationRunner(MongockSpring5.Builder mongockBuilder) {
    return mongockBuilder.buildApplicationRunner();
  }

  @Bean
  @ConditionalOnProperty(value = "mongock.runner-type", havingValue = "InitializingBean")
  public SpringInitializingBean mongockInitializingBeanRunner(MongockSpring5.Builder mongockBuilder) {
    return mongockBuilder.buildInitializingBeanRunner();
  }
}


