package com.github.cloudyrock.spring.v5;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.SpringDataMongo3Driver;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.changelogs.MongockSync4LegacyMigrationChangeLog;
import io.changock.migration.api.exception.ChangockException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongockSpringDataV3CoreContext extends MongockSpringDataCoreContextBase {

  @Bean
  public SpringDataMongo3Driver mongockConnectionDriver(MongoTemplate mongoTemplate,
                                                        MongockConfiguration mongockConfiguration) {
    try {
      SpringDataMongo3Driver driver = SpringDataMongo3Driver.withLockSetting(mongoTemplate, mongockConfiguration.getLockAcquiredForMinutes(), mongockConfiguration.getMaxWaitingForLockMinutes(), mongockConfiguration.getMaxTries());
      setUpMongockConnectionDriver(mongockConfiguration, driver);
      return driver;
    } catch (NoClassDefFoundError driver3NotFoundError) {
      throw new ChangockException("\n\n" + ConfigErrorMessages.DRIVER_NOT_FOUND_ERROR + "\n\n");
    }
  }

  @Bean
  public MongockSpring5.Builder mongockBuilder(SpringDataMongo3Driver mongockConnectionDriver,
                                               MongockConfiguration mongockConfiguration,
                                               ApplicationContext springContext) {
    return super.mongockBuilder(mongockConnectionDriver, mongockConfiguration, springContext);
  }

  protected void setLegacyMigrationChangeLog(MongockSpring5.Builder builder, MongockConfiguration mongockConfiguration) {
    if (mongockConfiguration.getLegacyMigration() != null) {
      try {
        builder.addChangeLogsScanPackage(MongockSync4LegacyMigrationChangeLog.class.getPackage().getName());
      } catch (NoClassDefFoundError mongockDriverSyncV4NotFoundError) {
        throw new ChangockException("\n\n" + ConfigErrorMessages.DRIVER_NOT_FOUND_ERROR + "\n\n");
      }
    }
  }

}
