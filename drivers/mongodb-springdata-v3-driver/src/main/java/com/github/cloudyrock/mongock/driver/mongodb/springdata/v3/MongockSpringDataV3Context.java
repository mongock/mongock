package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3;

import io.changock.driver.api.driver.ConnectionDriver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

@Configuration
@ConditionalOnProperty(prefix = "mongock", name = "enabled", matchIfMissing = true, havingValue = "true")
public class MongockSpringDataV3Context {

  @Bean
  public ConnectionDriver mongockConnectionDriver(MongoTemplate mongoTemplate,
                                                  MongockSpringDataV3Configuration mongockSpringConfiguration,
                                                  Optional<MongoTransactionManager> txManagerOpt) {
    SpringDataMongo3Driver driver = getDriver(mongoTemplate, mongockSpringConfiguration, txManagerOpt);
    setUpMongockConnectionDriver(mongockSpringConfiguration, driver);
    return driver;
  }

  private SpringDataMongo3Driver getDriver(MongoTemplate mongoTemplate,
                                           MongockSpringDataV3Configuration mongockSpringConfiguration,
                                           Optional<MongoTransactionManager> txManagerOpt) {
    SpringDataMongo3Driver driver = SpringDataMongo3Driver.withLockSetting(mongoTemplate, mongockSpringConfiguration.getLockAcquiredForMinutes(), mongockSpringConfiguration.getMaxWaitingForLockMinutes(), mongockSpringConfiguration.getMaxTries());
    if (mongockSpringConfiguration.isTransactionEnabled() && txManagerOpt.isPresent()) {
      txManagerOpt.ifPresent(driver::enableTransactionWithTxManager);
    } else {
      driver.disableTransaction();
    }
    return driver;
  }

  private void setUpMongockConnectionDriver(MongockSpringDataV3Configuration mongockSpringConfiguration,
                                            SpringDataMongo3Driver driver) {
    driver.setChangeLogCollectionName(mongockSpringConfiguration.getChangeLogCollectionName());
    driver.setLockCollectionName(mongockSpringConfiguration.getLockCollectionName());
    driver.setIndexCreation(mongockSpringConfiguration.isIndexCreation());
    driver.initialize();
  }

}
