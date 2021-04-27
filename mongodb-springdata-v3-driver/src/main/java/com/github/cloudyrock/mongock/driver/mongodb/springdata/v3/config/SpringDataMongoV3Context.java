package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.config;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.SpringDataMongoV3Driver;
import com.mongodb.ReadConcern;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

@Configuration
@Import(MongoDBConfiguration.class)
@ConditionalOnExpression("${mongock.enabled:true}")
public class SpringDataMongoV3Context {

  @Bean
  public ConnectionDriver connectionDriver(MongoTemplate mongoTemplate,
                                           MongockConfiguration config,
                                           MongoDBConfiguration mongoDbConfig,
                                           Optional<MongoTransactionManager> txManagerOpt) {
    SpringDataMongoV3Driver driver = buildDriver(mongoTemplate, config, mongoDbConfig, txManagerOpt);
    driver.initialize();
    return driver;
  }

  private SpringDataMongoV3Driver buildDriver(MongoTemplate mongoTemplate,
                                              MongockConfiguration config,
                                              MongoDBConfiguration mongoDbConfig,
                                              Optional<MongoTransactionManager> txManagerOpt) {
    SpringDataMongoV3Driver driver = SpringDataMongoV3Driver.withLockStrategy(
        mongoTemplate,
        config.getLockAcquiredForMillis(),
        config.getLockQuitTryingAfterMillis(),
        config.getLockTryFrequencyMillis());
    setGenericDriverConfig(config, txManagerOpt, driver);
    setMongoDBConfig(mongoDbConfig, driver);
    return driver;
  }


  private void setGenericDriverConfig(MongockConfiguration config,
                                      Optional<MongoTransactionManager> txManagerOpt,
                                      SpringDataMongoV3Driver driver) {
    setTransactionManager(config, txManagerOpt, driver);
    driver.setChangeLogRepositoryName(config.getChangeLogRepositoryName());
    driver.setLockRepositoryName(config.getLockRepositoryName());
    driver.setIndexCreation(config.isIndexCreation());
  }

  private void setMongoDBConfig(MongoDBConfiguration mongoDbConfig, SpringDataMongoV3Driver driver) {
    driver.setWriteConcern(mongoDbConfig.getBuiltMongoDBWriteConcern());
    driver.setReadConcern(new ReadConcern(mongoDbConfig.getReadConcern()));
    driver.setReadPreference(mongoDbConfig.getReadPreference().getValue());
  }



  private void setTransactionManager(MongockConfiguration config,
                                     Optional<MongoTransactionManager> txManagerOpt,
                                     SpringDataMongoV3Driver driver) {
    txManagerOpt
        .filter(tx-> config.isTransactionEnabled())
        .map(tx-> {
          driver.enableTransactionWithTxManager(tx);
          return true;
        }).orElseGet(()-> {
          driver.disableTransaction();
          return false;
        });

  }


}
