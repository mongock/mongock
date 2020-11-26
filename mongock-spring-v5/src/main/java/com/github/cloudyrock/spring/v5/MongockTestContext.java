package com.github.cloudyrock.spring.v5;

import com.github.cloudyrock.mongock.MongockConnectionDriver;
import io.changock.driver.api.driver.ConnectionDriver;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(MongockCoreContextSelector.class)
@ConditionalOnProperty(prefix = "mongock", name = "enabled", matchIfMissing = true, havingValue = "true")
public class MongockTestContext {


  @Bean
  public MongockTestDriverInitializingBean mongockTestDriverInitializingBean(MongockConnectionDriver connectionDriver) {
    return new MongockTestDriverInitializingBean(connectionDriver);
  }


  public static class MongockTestDriverInitializingBean implements InitializingBean {

    private final ConnectionDriver driver;

    private MongockTestDriverInitializingBean(ConnectionDriver driver) {
      this.driver = driver;
    }

    @Override
    public void afterPropertiesSet() {
      // As it's a test environment we need to ensure the lock is released before acquiring it
      driver.getLockManager().clean();
      driver.getLockManager().acquireLockDefault();
    }
  }
}
