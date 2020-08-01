package com.github.cloudyrock.spring.v5;

import com.github.cloudyrock.mongock.MongockConnectionDriver;
import org.springframework.context.ApplicationContext;

public abstract class MongockSpringDataCoreContextBase {

  protected MongockSpring5.Builder mongockBuilder(MongockConnectionDriver mongockConnectionDriver,
                                                  MongockConfiguration mongockConfiguration,
                                                  ApplicationContext springContext) {
    MongockSpring5.Builder builder = MongockSpring5.builder()
        .setDriver(mongockConnectionDriver)
        .setConfig(mongockConfiguration)
        .setSpringContext(springContext);
    return builder;
  }

  protected void setUpMongockConnectionDriver(MongockConfiguration mongockConfiguration,
                                              MongockConnectionDriver driver) {
    driver.setChangeLogCollectionName(mongockConfiguration.getChangeLogCollectionName());
    driver.setLockCollectionName(mongockConfiguration.getLockCollectionName());
    driver.setIndexCreation(mongockConfiguration.isIndexCreation());
    driver.initialize();
  }
}
