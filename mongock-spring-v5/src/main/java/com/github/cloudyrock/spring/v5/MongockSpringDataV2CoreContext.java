package com.github.cloudyrock.spring.v5;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.SpringDataMongo2Driver;
import com.github.cloudyrock.mongock.driver.mongodb.v3.changelogs.runalways.MongockV3LegacyMigrationChangeRunAlwaysLog;
import com.github.cloudyrock.mongock.driver.mongodb.v3.changelogs.runonce.MongockV3LegacyMigrationChangeLog;
import io.changock.migration.api.exception.ChangockException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

@Configuration
public class MongockSpringDataV2CoreContext extends MongockSpringDataCoreContextBase {

  @Bean
  public SpringDataMongo2Driver mongockConnectionDriver(MongoTemplate mongoTemplate,
                                                        MongockConfiguration mongockConfiguration,
                                                        Optional<MongoTransactionManager> txManagerOpt) {
    try {
      SpringDataMongo2Driver driver = SpringDataMongo2Driver.withLockSetting(mongoTemplate, mongockConfiguration.getLockAcquiredForMinutes(), mongockConfiguration.getMaxWaitingForLockMinutes(), mongockConfiguration.getMaxTries());
      if(mongockConfiguration.isTransactionEnabled() && txManagerOpt.isPresent()) {
        txManagerOpt.ifPresent(driver::enableTransactionWithTxManager);
      } else {
        driver.disableTransaction();
      }
      setUpMongockConnectionDriver(mongockConfiguration, driver);
      return driver;
    } catch (NoClassDefFoundError driver2NotFoundError) {
      throw new ChangockException("\n\n" + ConfigErrorMessageUtils.getDriverNotFoundErrorMessage() + "\n\n");
    }
  }

  @Bean
  public MongockSpring5.Builder mongockBuilder(SpringDataMongo2Driver mongockConnectionDriver,
                                               MongockConfiguration mongockConfiguration,
                                               ApplicationContext springContext) {
    return super.mongockBuilder(mongockConnectionDriver, mongockConfiguration, springContext);
  }

}
