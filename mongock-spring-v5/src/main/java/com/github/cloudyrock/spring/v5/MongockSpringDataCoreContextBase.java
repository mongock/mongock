package com.github.cloudyrock.spring.v5;

import com.github.cloudyrock.mongock.MongockConnectionDriver;
import io.changock.migration.api.exception.ChangockException;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.util.StringUtils;

import java.util.Optional;

public abstract class MongockSpringDataCoreContextBase {

  protected MongockSpring5.Builder mongockBuilder(MongockConnectionDriver mongockConnectionDriver,
                                                  MongockConfiguration mongockConfiguration,
                                                  ApplicationContext springContext) {
    MongockSpring5.Builder builder = MongockSpring5.builder()
        .setDriver(mongockConnectionDriver)
        .setConfig(mongockConfiguration)
        .setSpringContext(springContext);
    setLegacyMigrationChangeLog(builder, mongockConfiguration);
    return builder;
  }

  protected void setUpMongockConnectionDriver(MongockConfiguration mongockConfiguration,
                                              MongockConnectionDriver driver) {
    driver.setChangeLogCollectionName(mongockConfiguration.getChangeLogCollectionName());
    driver.setLockCollectionName(mongockConfiguration.getLockCollectionName());
    driver.setIndexCreation(mongockConfiguration.isIndexCreation());
    driver.initialize();
  }

  protected abstract void setLegacyMigrationChangeLog(MongockSpring5.Builder builder, MongockConfiguration mongockConfiguration);
}
