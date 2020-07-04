package com.github.cloudyrock.spring.v5;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.SpringDataMongo2Driver;
import com.github.cloudyrock.mongock.driver.mongodb.v3.changelogs.MongockV3LegacyMigrationChangeLog;
import io.changock.migration.api.exception.ChangockException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongockSpringDataV2CoreContext extends MongockSpringDataCoreContextBase {

  @Bean
  public SpringDataMongo2Driver mongockConnectionDriver(MongoTemplate mongoTemplate,
                                                        MongockConfiguration mongockConfiguration) {
    try {
      SpringDataMongo2Driver driver = SpringDataMongo2Driver.withLockSetting(mongoTemplate, mongockConfiguration.getLockAcquiredForMinutes(), mongockConfiguration.getMaxWaitingForLockMinutes(), mongockConfiguration.getMaxTries());
      setUpMongockConnectionDriver(mongockConfiguration, driver);
      return driver;
    } catch (NoClassDefFoundError driver2NotFoundError) {
      throw new ChangockException("\n\n" + ConfigErrorMessages.DRIVER_NOT_FOUND_ERROR + "\n\n");
    }
  }

  @Bean
  public MongockSpring5.Builder mongockBuilder(SpringDataMongo2Driver mongockConnectionDriver,
                                               MongockConfiguration mongockConfiguration,
                                               ApplicationContext springContext) {
    return super.mongockBuilder(mongockConnectionDriver, mongockConfiguration, springContext);
  }

  protected void setLegacyMigrationChangeLog(MongockSpring5.Builder builder, MongockConfiguration mongockConfiguration) {
    if (mongockConfiguration.getLegacyMigration() != null) {
      try {
        builder.addChangeLogsScanPackage(MongockV3LegacyMigrationChangeLog.class.getPackage().getName());
      } catch (NoClassDefFoundError mongockDriverV3NotFoundError) {
        throw new ChangockException("\n\n" + ConfigErrorMessages.DRIVER_NOT_FOUND_ERROR + "\n\n");
      }
    }
  }
}
