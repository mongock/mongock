package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.config;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.SpringDataMongoV2DriverBase;
import com.mongodb.ReadConcern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

public abstract class SpringDataMongoV2ContextBase<CHANGE_ENTRY extends ChangeEntry, CONFIG extends MongockConfiguration, DRIVER extends SpringDataMongoV2DriverBase> {
  private static final Logger logger = LoggerFactory.getLogger(SpringDataMongoV2ContextBase.class);

  @Bean
  public ConnectionDriver<CHANGE_ENTRY> connectionDriver(MongoTemplate mongoTemplate,
                                                        CONFIG config,
                                                        MongoDBConfiguration mongoDbConfig,
                                                        Optional<MongoTransactionManager> txManagerOpt) {
    DRIVER driver = buildDriver(mongoTemplate, config, mongoDbConfig, txManagerOpt);
    setGenericDriverConfig(config, txManagerOpt, driver);
    setMongoDBConfig(mongoDbConfig, driver);
    driver.initialize();
    return driver;
  }

  protected abstract DRIVER buildDriver(MongoTemplate mongoTemplate,
                                        CONFIG config,
                                        MongoDBConfiguration mongoDbConfig,
                                        Optional<MongoTransactionManager> txManagerOpt);

  private void setGenericDriverConfig(CONFIG config,
                                              Optional<MongoTransactionManager> txManagerOpt,
                                              DRIVER driver) {
    txManagerOpt.ifPresent(driver::enableTransactionWithTxManager);
    driver.setChangeLogRepositoryName(config.getChangeLogRepositoryName());
    driver.setLockRepositoryName(config.getLockRepositoryName());
    driver.setIndexCreation(config.isIndexCreation());
  }

  private void setMongoDBConfig(MongoDBConfiguration mongoDbConfig, DRIVER driver) {
    driver.setWriteConcern(mongoDbConfig.getBuiltMongoDBWriteConcern());
    driver.setReadConcern(new ReadConcern(mongoDbConfig.getReadConcern()));
    driver.setReadPreference(mongoDbConfig.getReadPreference().getValue());
  }

}
