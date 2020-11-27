package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2;

import io.changock.driver.api.driver.ConnectionDriver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

@Configuration
@ConditionalOnExpression("${mongock.enabled:true} && ${changock.enabled:true}")
public class SpringDataMongoV2Context {

  @Bean
  public ConnectionDriver connectionDriver(MongoTemplate mongoTemplate,
                                           MongockSpringDataV2Configuration config,
                                           Optional<MongoTransactionManager> txManagerOpt) {
    SpringDataMongoV2Driver driver = getDriver(mongoTemplate, config, txManagerOpt);
    setUpConnectionDriver(config, driver);
    return driver;
  }

  private SpringDataMongoV2Driver getDriver(MongoTemplate mongoTemplate,
                                            MongockSpringDataV2Configuration config,
                                            Optional<MongoTransactionManager> txManagerOpt) {
    SpringDataMongoV2Driver driver = SpringDataMongoV2Driver.withLockSetting(mongoTemplate, config.getLockAcquiredForMinutes(), config.getMaxWaitingForLockMinutes(), config.getMaxTries());
    if (config.isTransactionEnabled() && txManagerOpt.isPresent()) {
      txManagerOpt.ifPresent(driver::enableTransactionWithTxManager);
    } else {
      driver.disableTransaction();
    }
    return driver;
  }

  private void setUpConnectionDriver(MongockSpringDataV2Configuration config,
                                     SpringDataMongoV2Driver driver) {
    driver.setChangeLogCollectionName(config.getChangeLogCollectionName());
    driver.setLockCollectionName(config.getLockCollectionName());
    driver.setIndexCreation(config.isIndexCreation());
    driver.initialize();
  }


}
