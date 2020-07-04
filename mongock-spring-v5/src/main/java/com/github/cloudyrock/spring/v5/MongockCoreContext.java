package com.github.cloudyrock.spring.v5;

import com.github.cloudyrock.mongock.MongockConnectionDriver;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.SpringDataMongo2Driver;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.SpringDataMongo3Driver;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.changelogs.MongockSync4LegacyMigrationChangeLog;
import com.github.cloudyrock.mongock.driver.mongodb.v3.changelogs.MongockV3LegacyMigrationChangeLog;
import io.changock.migration.api.exception.ChangockException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.StringUtils;

@Configuration
public class MongockCoreContext {

  @Bean
  public MongockConnectionDriver mongockConnectionDriver(MongoTemplate mongoTemplate,
                                                         MongockConfiguration mongockConfiguration) {
    MongockConnectionDriver driver;
    try {
      driver = SpringDataMongo3Driver.withLockSetting(mongoTemplate, mongockConfiguration.getLockAcquiredForMinutes(), mongockConfiguration.getMaxWaitingForLockMinutes(), mongockConfiguration.getMaxTries());
    } catch (NoClassDefFoundError driver3NotFoundError) {
      try {
        driver = SpringDataMongo2Driver.withLockSetting(mongoTemplate, mongockConfiguration.getLockAcquiredForMinutes(), mongockConfiguration.getMaxWaitingForLockMinutes(), mongockConfiguration.getMaxTries());
        return driver;
      } catch (NoClassDefFoundError driver2NotFoundError) {
        throw new ChangockException("\n\n" + ConfigErrorMessages.DRIVER_NOT_FOUND_ERROR + "\n\n");
      }
    }
    driver.setChangeLogCollectionName(mongockConfiguration.getChangeLogCollectionName());
    driver.setLockCollectionName(mongockConfiguration.getLockCollectionName());
    driver.setIndexCreation(mongockConfiguration.isIndexCreation());
    driver.initialize();
    return driver;
  }

  @Bean
  public MongockSpring5.Builder mongockBuilder(MongockConnectionDriver mongockConnectionDriver,
                                               MongockConfiguration mongockConfiguration,
                                               ApplicationContext springContext) {
    if (StringUtils.isEmpty(mongockConfiguration.getChangeLogsScanPackage())) {
      throw new ChangockException("\n\nMongock: You need to specify property: spring.mongock.changeLogsScanPackage\n\n");
    }
    MongockSpring5.Builder builder = MongockSpring5.builder()
        .setDriver(mongockConnectionDriver)
        .setConfig(mongockConfiguration)
        .setSpringContext(springContext);
    setLegacyMigrationChangeLog(builder, mongockConfiguration);
    return builder;
  }

  private void setLegacyMigrationChangeLog(MongockSpring5.Builder builder, MongockConfiguration mongockConfiguration) {
    if (mongockConfiguration.getLegacyMigration() != null) {
      try {
        builder.addChangeLogsScanPackage(MongockSync4LegacyMigrationChangeLog.class.getPackage().getName());
      } catch (NoClassDefFoundError mongockSyncDriverNotFoundError) {
        try {
          builder.addChangeLogsScanPackage(MongockV3LegacyMigrationChangeLog.class.getPackage().getName());
        } catch (NoClassDefFoundError mongockDriverV3NotFoundError) {
          throw new ChangockException("\n\n" + ConfigErrorMessages.DRIVER_NOT_FOUND_ERROR + "\n\n");
        }
      }
    }
  }


}
