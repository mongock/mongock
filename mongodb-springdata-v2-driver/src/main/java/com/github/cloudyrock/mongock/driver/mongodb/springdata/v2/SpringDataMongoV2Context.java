package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2;

import com.github.cloudyrock.mongock.config.MongockSpringConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

@Configuration
@ConditionalOnExpression("${mongock.enabled:true}")
public class SpringDataMongoV2Context {

  @Bean
  public ConnectionDriver connectionDriver(MongoTemplate mongoTemplate,
                                           MongockSpringConfiguration config,
                                           Optional<MongoTransactionManager> txManagerOpt) {
    SpringDataMongoV2Driver driver = getDriver(mongoTemplate, config, txManagerOpt);
    setUpConnectionDriver(config, driver);
    return driver;
  }

  private SpringDataMongoV2Driver getDriver(MongoTemplate mongoTemplate,
                                            MongockSpringConfiguration config,
                                            Optional<MongoTransactionManager> txManagerOpt) {
    SpringDataMongoV2Driver driver = SpringDataMongoV2Driver.withLockSetting(mongoTemplate, config.getLockAcquiredForMinutes(), config.getMaxWaitingForLockMinutes(), config.getMaxTries());
    if (config.isTransactionEnabled() && txManagerOpt.isPresent()) {
      txManagerOpt.ifPresent(driver::enableTransactionWithTxManager);
    } else {
      driver.disableTransaction();
    }
    return driver;
  }

  private void setUpConnectionDriver(MongockSpringConfiguration config,
                                     SpringDataMongoV2Driver driver) {
    driver.setChangeLogRepositoryName(config.getChangeLogRepositoryName());
    driver.setLockRepositoryName(config.getLockRepositoryName());
    driver.setIndexCreation(config.isIndexCreation());
    driver.initialize();
  }


}
