package com.github.cloudyrock.spring.v5;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.SpringDataMongo3Driver;
import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.migration.api.exception.ChangockException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

@Configuration
@ConditionalOnProperty(prefix = "mongock", name = "enabled", matchIfMissing = true, havingValue = "true")
public class MongockSpringDataV3CoreContext {

  @Bean
  public ConnectionDriver mongockConnectionDriver(MongoTemplate mongoTemplate,
                                                  MongockConfiguration mongockConfiguration,
                                                  Optional<MongoTransactionManager> txManagerOpt) {
    try {
      SpringDataMongo3Driver driver = getDriver(mongoTemplate, mongockConfiguration, txManagerOpt);
      setUpMongockConnectionDriver(mongockConfiguration, driver);
      return driver;
    } catch (NoClassDefFoundError driver3NotFoundError) {
      throw new ChangockException("\n\n" + ConfigErrorMessageUtils.getDriverNotFoundErrorMessage() + "\n\n");
    }
  }

  private SpringDataMongo3Driver getDriver(MongoTemplate mongoTemplate,
                                           MongockConfiguration mongockConfiguration,
                                           Optional<MongoTransactionManager> txManagerOpt) {
    SpringDataMongo3Driver driver = SpringDataMongo3Driver.withLockSetting(mongoTemplate, mongockConfiguration.getLockAcquiredForMinutes(), mongockConfiguration.getMaxWaitingForLockMinutes(), mongockConfiguration.getMaxTries());
    if (mongockConfiguration.isTransactionEnabled() && txManagerOpt.isPresent()) {
      txManagerOpt.ifPresent(driver::enableTransactionWithTxManager);
    } else {
      driver.disableTransaction();
    }
    return driver;
  }

  private void setUpMongockConnectionDriver(MongockConfiguration mongockConfiguration,
                                            SpringDataMongo3Driver driver) {
    driver.setChangeLogCollectionName(mongockConfiguration.getChangeLogCollectionName());
    driver.setLockCollectionName(mongockConfiguration.getLockCollectionName());
    driver.setIndexCreation(mongockConfiguration.isIndexCreation());
    driver.initialize();
  }

}
