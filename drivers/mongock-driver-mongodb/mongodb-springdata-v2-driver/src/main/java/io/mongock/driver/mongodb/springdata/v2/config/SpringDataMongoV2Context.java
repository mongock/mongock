package io.mongock.driver.mongodb.springdata.v2.config;

import io.mongock.api.config.MongockConfiguration;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.driver.mongodb.springdata.v2.SpringDataMongoV2Driver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

@Configuration
@Import(MongoDBConfiguration.class)
@ConditionalOnExpression("${mongock.enabled:true}")
public class SpringDataMongoV2Context extends SpringDataMongoV2ContextBase<ChangeEntry, MongockConfiguration, SpringDataMongoV2Driver> {

  @Override
  protected SpringDataMongoV2Driver buildDriver(MongoTemplate mongoTemplate,
                                              MongockConfiguration config,
                                              MongoDBConfiguration mongoDbConfig,
                                              Optional<MongoTransactionManager> txManagerOpt) {
    return SpringDataMongoV2Driver.withLockStrategy(
        mongoTemplate,
        config.getLockAcquiredForMillis(),
        config.getLockQuitTryingAfterMillis(),
        config.getLockTryFrequencyMillis());
  }

}
