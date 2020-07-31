package com.github.cloudyrock.spring.v5;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.SpringDataMongo3Driver;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.changelogs.runalways.MongockSync4LegacyMigrationChangeRunAlwaysLog;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.changelogs.runonce.MongockSync4LegacyMigrationChangeLog;
import io.changock.migration.api.exception.ChangockException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

@Configuration
public class MongockSpringDataV3CoreContext extends MongockSpringDataCoreContextBase {

  @Bean
  public SpringDataMongo3Driver mongockConnectionDriver(MongoTemplate mongoTemplate,
                                                        MongockConfiguration mongockConfiguration,
                                                        Optional<MongoTransactionManager> txManagerOpt) {
    try {
      SpringDataMongo3Driver driver = SpringDataMongo3Driver.withLockSetting(mongoTemplate, mongockConfiguration.getLockAcquiredForMinutes(), mongockConfiguration.getMaxWaitingForLockMinutes(), mongockConfiguration.getMaxTries());
      if (mongockConfiguration.isTransactionEnabled() && txManagerOpt.isPresent()) {
        txManagerOpt.ifPresent(driver::enableTransactionWithTxManager);
      } else {
        driver.disableTransaction();
      }
      setUpMongockConnectionDriver(mongockConfiguration, driver);
      return driver;
    } catch (NoClassDefFoundError driver3NotFoundError) {
      throw new ChangockException("\n\n" + ConfigErrorMessageUtils.getDriverNotFoundErrorMessage() + "\n\n");
    }
  }

  @Bean
  public MongockSpring5.Builder mongockBuilder(SpringDataMongo3Driver mongockConnectionDriver,
                                               MongockConfiguration mongockConfiguration,
                                               ApplicationContext springContext) {
    return super.mongockBuilder(mongockConnectionDriver, mongockConfiguration, springContext);
  }


}
