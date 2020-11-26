package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3;

import com.github.cloudyrock.mongock.config.MongockSpringConfigurationBase;
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
                                                  MongockSpringConfigurationBase mongockSpringConfiguration,
                                                  Optional<MongoTransactionManager> txManagerOpt) {
    try {
      SpringDataMongo3Driver driver = getDriver(mongoTemplate, mongockSpringConfiguration, txManagerOpt);
      setUpMongockConnectionDriver(mongockSpringConfiguration, driver);
      return driver;
    } catch (NoClassDefFoundError driver3NotFoundError) {
      throw new ChangockException("\n\n" + ConfigErrorMessageUtils.getDriverNotFoundErrorMessage() + "\n\n");
    }
  }

  private SpringDataMongo3Driver getDriver(MongoTemplate mongoTemplate,
                                           MongockSpringConfigurationBase mongockSpringConfiguration,
                                           Optional<MongoTransactionManager> txManagerOpt) {
    SpringDataMongo3Driver driver = SpringDataMongo3Driver.withLockSetting(mongoTemplate, mongockSpringConfiguration.getLockAcquiredForMinutes(), mongockSpringConfiguration.getMaxWaitingForLockMinutes(), mongockSpringConfiguration.getMaxTries());
    if (mongockSpringConfiguration.isTransactionEnabled() && txManagerOpt.isPresent()) {
      txManagerOpt.ifPresent(driver::enableTransactionWithTxManager);
    } else {
      driver.disableTransaction();
    }
    return driver;
  }

  private void setUpMongockConnectionDriver(MongockSpringConfigurationBase mongockSpringConfiguration,
                                            SpringDataMongo3Driver driver) {
    driver.setChangeLogCollectionName(mongockSpringConfiguration.getChangeLogCollectionName());
    driver.setLockCollectionName(mongockSpringConfiguration.getLockCollectionName());
    driver.setIndexCreation(mongockSpringConfiguration.isIndexCreation());
    driver.initialize();
  }

}
