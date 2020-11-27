package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3;

import io.changock.driver.api.driver.ConnectionDriver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

@Configuration
@ConditionalOnExpression("${mongock.enabled:true} && ${changock.enabled:true}")
public class SpringDataV3Context {

  @Bean
  public ConnectionDriver connectionDriver(MongoTemplate mongoTemplate,
                                           SpringDataMongoV3Configuration config,
                                           Optional<MongoTransactionManager> txManagerOpt) {
    SpringDataMongoV3Driver driver = getDriver(mongoTemplate, config, txManagerOpt);
    setUpConnectionDriver(config, driver);
    return driver;
  }

  private SpringDataMongoV3Driver getDriver(MongoTemplate mongoTemplate,
                                            SpringDataMongoV3Configuration config,
                                            Optional<MongoTransactionManager> txManagerOpt) {
    SpringDataMongoV3Driver driver = SpringDataMongoV3Driver.withLockSetting(mongoTemplate, config.getLockAcquiredForMinutes(), config.getMaxWaitingForLockMinutes(), config.getMaxTries());
    if (config.isTransactionEnabled() && txManagerOpt.isPresent()) {
      txManagerOpt.ifPresent(driver::enableTransactionWithTxManager);
    } else {
      driver.disableTransaction();
    }
    return driver;
  }

  private void setUpConnectionDriver(SpringDataMongoV3Configuration config,
                                     SpringDataMongoV3Driver driver) {
    driver.setChangeLogCollectionName(config.getChangeLogCollectionName());
    driver.setLockCollectionName(config.getLockCollectionName());
    driver.setIndexCreation(config.isIndexCreation());
    driver.initialize();
  }

}
