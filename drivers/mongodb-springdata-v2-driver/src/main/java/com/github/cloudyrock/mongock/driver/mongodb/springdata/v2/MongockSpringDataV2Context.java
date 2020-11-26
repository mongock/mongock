package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2;

import com.github.cloudyrock.mongock.config.MongockSpringConfigurationBase;
import io.changock.driver.api.driver.ConnectionDriver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

@Configuration
@ConditionalOnExpression("${mongock.enabled:true} && ${changock.enabled:true}")
public class MongockSpringDataV2Context {

  @Bean
  public ConnectionDriver mongockConnectionDriver(MongoTemplate mongoTemplate,
                                                  MongockSpringDataV2Configuration mongockSpringConfiguration,
                                                  Optional<MongoTransactionManager> txManagerOpt) {
    SpringDataMongo2Driver driver = getDriver(mongoTemplate, mongockSpringConfiguration, txManagerOpt);
    setUpMongockConnectionDriver(mongockSpringConfiguration, driver);
    return driver;
  }

  private SpringDataMongo2Driver getDriver(MongoTemplate mongoTemplate, MongockSpringConfigurationBase mongockSpringConfiguration, Optional<MongoTransactionManager> txManagerOpt) {
    SpringDataMongo2Driver driver = SpringDataMongo2Driver.withLockSetting(mongoTemplate, mongockSpringConfiguration.getLockAcquiredForMinutes(), mongockSpringConfiguration.getMaxWaitingForLockMinutes(), mongockSpringConfiguration.getMaxTries());
    if (mongockSpringConfiguration.isTransactionEnabled() && txManagerOpt.isPresent()) {
      txManagerOpt.ifPresent(driver::enableTransactionWithTxManager);
    } else {
      driver.disableTransaction();
    }
    return driver;
  }

  private void setUpMongockConnectionDriver(MongockSpringDataV2Configuration mongockSpringConfiguration,
                                            SpringDataMongo2Driver driver) {
    driver.setChangeLogCollectionName(mongockSpringConfiguration.getChangeLogCollectionName());
    driver.setLockCollectionName(mongockSpringConfiguration.getLockCollectionName());
    driver.setIndexCreation(mongockSpringConfiguration.isIndexCreation());
    driver.initialize();
  }


}
