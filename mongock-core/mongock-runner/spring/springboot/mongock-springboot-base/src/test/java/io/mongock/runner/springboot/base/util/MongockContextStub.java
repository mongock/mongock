package io.mongock.runner.springboot.base.util;

import io.mongock.api.config.MongockConfiguration;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.runner.springboot.base.MongockApplicationRunner;
import io.mongock.runner.springboot.base.MongockInitializingBeanRunner;
import io.mongock.runner.springboot.base.builder.SpringApplicationBean;
import io.mongock.runner.springboot.base.config.MongockContextBase;
import io.mongock.runner.springboot.base.config.MongockSpringConfiguration;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MongockSpringConfiguration.class})
@ConditionalOnExpression("${mongock.enabled:true}")
public class MongockContextStub extends MongockContextBase<MongockConfiguration> {


  @Bean
  public ConnectionDriver connectionDriver() {
    return Mockito.mock(ConnectionDriver.class);
  }


  @Override
  public SpringApplicationBean getBuilder(ConnectionDriver connectionDriver,
                                          MongockConfiguration springConfiguration,
                                          ApplicationContext springContext,
                                          ApplicationEventPublisher applicationEventPublisher) {
    return new SpringApplicationBean() {
      @Override
      public MongockApplicationRunner buildApplicationRunner() {
        return Mockito.mock(MongockApplicationRunner.class);
      }

      @Override
      public MongockInitializingBeanRunner buildInitializingBeanRunner() {
        return Mockito.mock(MongockInitializingBeanRunner.class);
      }
    };
  }
}
