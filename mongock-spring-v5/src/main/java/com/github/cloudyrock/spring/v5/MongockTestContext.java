package com.github.cloudyrock.spring.v5;

import com.github.cloudyrock.mongock.MongockConnectionDriver;
import org.bson.BsonDocument;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@Import(MongockCoreContextSelector.class)
@ConditionalOnProperty(prefix = "mongock", name = "enabled", matchIfMissing = true, havingValue = "true")
public class MongockTestContext {


  @Bean
  public MongockTestDriverInitializingBean mongockTestDriverInitializingBean(MongockConnectionDriver connectionDriver,
                                                                             MongoTemplate mongoTemplate) {
    return new MongockTestDriverInitializingBean(connectionDriver, mongoTemplate);
  }


  public static class MongockTestDriverInitializingBean implements InitializingBean {

    private final MongockConnectionDriver driver;
    private final MongoTemplate mongoTemplate;

    private MongockTestDriverInitializingBean(MongockConnectionDriver driver,
                                              MongoTemplate mongoTemplate) {
      this.driver = driver;
      this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void afterPropertiesSet() {
      // As it's a test environment we need to ensure the lock is released before acquiring it
      mongoTemplate.getCollection(driver.getLockCollectionName()).deleteMany(new BsonDocument());
      driver.getLockManager().acquireLockDefault();
    }
  }
}
