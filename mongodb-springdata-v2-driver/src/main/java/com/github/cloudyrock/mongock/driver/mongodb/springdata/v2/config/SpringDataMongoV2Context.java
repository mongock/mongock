package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.config;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.SpringDataMongoV2Driver;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.mongodb.ReadConcern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class SpringDataMongoV2Context {
  private static final Logger logger = LoggerFactory.getLogger(SpringDataMongoV2Context.class);

  @Bean
  public ConnectionDriver connectionDriver(MongoTemplate mongoTemplate,
                                           MongockConfiguration config,
                                           MongoDBConfiguration mongoDbConfig,
                                           Optional<MongoTransactionManager> txManagerOpt) {
    SpringDataMongoV2Driver driver = buildDriver(mongoTemplate, config, mongoDbConfig, txManagerOpt);
    driver.initialize();
    return driver;
  }

  private SpringDataMongoV2Driver buildDriver(MongoTemplate mongoTemplate,
                                              MongockConfiguration config,
                                              MongoDBConfiguration mongoDbConfig,
                                              Optional<MongoTransactionManager> txManagerOpt) {
    SpringDataMongoV2Driver driver = SpringDataMongoV2Driver.withLockStrategy(
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
                                      SpringDataMongoV2Driver driver) {
    setTransactionManager(config, txManagerOpt, driver);
    driver.setChangeLogRepositoryName(config.getChangeLogRepositoryName());
    driver.setLockRepositoryName(config.getLockRepositoryName());
    driver.setIndexCreation(config.isIndexCreation());
  }

  private void setMongoDBConfig(MongoDBConfiguration mongoDbConfig, SpringDataMongoV2Driver driver) {
    driver.setWriteConcern(mongoDbConfig.getBuiltMongoDBWriteConcern());
    driver.setReadConcern(new ReadConcern(mongoDbConfig.getReadConcern()));
    driver.setReadPreference(mongoDbConfig.getReadPreference().getValue());
  }



  private void setTransactionManager(MongockConfiguration config,
                                     Optional<MongoTransactionManager> txManagerOpt,
                                     SpringDataMongoV2Driver driver) {
    //transaction-enabled explicitly set to true o false
    if (config.getTransactionEnabled().isPresent()) {
      boolean transactionEnabled = config.getTransactionEnabled().get();
      if (transactionEnabled) {
        MongoTransactionManager txManger = txManagerOpt.orElseThrow(() -> new MongockException("property transaction-enabled=true, but transactionManger not provided"));
        driver.enableTransactionWithTxManager(txManger);
      } else {
        if (txManagerOpt.isPresent()) {
          logger.warn("property transaction-enabled=false, but transactionManger is present");
        }
      }
    } else { //transaction-enabled not set
      txManagerOpt.ifPresent(driver::enableTransactionWithTxManager);
    }
  }
}
