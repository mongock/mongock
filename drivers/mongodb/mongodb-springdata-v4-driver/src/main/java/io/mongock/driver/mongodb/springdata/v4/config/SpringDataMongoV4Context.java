package io.mongock.driver.mongodb.springdata.v4.config;

import io.mongock.api.config.MongockConfiguration;
import io.mongock.driver.mongodb.springdata.v4.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

@AutoConfiguration
@ConditionalOnExpression("${mongock.enabled:true}")
@ConditionalOnBean(MongockConfiguration.class)
@EnableConfigurationProperties(MongoDBConfiguration.class)
public class SpringDataMongoV4Context extends SpringDataMongoV4ContextBase<MongockConfiguration, SpringDataMongoV4Driver> {

  @Override
  protected SpringDataMongoV4Driver buildDriver(MongoTemplate mongoTemplate,
                                                MongockConfiguration config,
                                                MongoDBConfiguration mongoDbConfig,
                                                Optional<PlatformTransactionManager> txManagerOpt) {
    return SpringDataMongoV4Driver.withLockStrategy(
        mongoTemplate,
        config.getLockAcquiredForMillis(),
        config.getLockQuitTryingAfterMillis(),
        config.getLockTryFrequencyMillis());
  }

}

