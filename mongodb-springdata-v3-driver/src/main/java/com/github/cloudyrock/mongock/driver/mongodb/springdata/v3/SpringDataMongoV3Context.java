package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3;

import com.github.cloudyrock.mongock.config.MongockSpringConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.mongodb.ReadConcern;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

@Configuration
@ConditionalOnExpression("${mongock.enabled:true}")
public class SpringDataMongoV3Context {

  @Bean
  public ConnectionDriver connectionDriver(MongoTemplate mongoTemplate,
                                           SpringMongoDBConfiguration config,
                                           Optional<MongoTransactionManager> txManagerOpt) {
    SpringDataMongoV3Driver driver = SpringDataMongoV3Driver.withLockSetting(
        mongoTemplate,
        config.getLockAcquiredForMinutes(),
        config.getMaxWaitingForLockMinutes(),
        config.getMaxTries());
    setGenericDriverConfig(config, txManagerOpt, driver);
    setMongoDBConfig(config, driver);
    driver.initialize();
    return driver;
  }

  private void setGenericDriverConfig(SpringMongoDBConfiguration config, Optional<MongoTransactionManager> txManagerOpt, SpringDataMongoV3Driver driver) {
    if(setTransactionOrFalseInstead(config, txManagerOpt, driver)) {
      driver.disableTransaction();
    }
    driver.setChangeLogRepositoryName(config.getChangeLogRepositoryName());
    driver.setLockRepositoryName(config.getLockRepositoryName());
    driver.setIndexCreation(config.isIndexCreation());
  }

  private void setMongoDBConfig(SpringMongoDBConfiguration config, SpringDataMongoV3Driver driver) {
    driver.setWriteConcern(config.getMongoDb().getBuiltMongoDBWriteConcern());
    driver.setReadConcern(new ReadConcern(config.getMongoDb().getReadConcern()));
    driver.setReadPreference(config.getMongoDb().getReadPreference().getValue());
  }



  private Boolean setTransactionOrFalseInstead(SpringMongoDBConfiguration config, Optional<MongoTransactionManager> txManagerOpt, SpringDataMongoV3Driver driver) {
    return txManagerOpt
        .filter(tx-> config.isTransactionEnabled())
        .map(tx-> {
          driver.enableTransactionWithTxManager(tx);
          return false;})
        .orElse(true);
  }


}
