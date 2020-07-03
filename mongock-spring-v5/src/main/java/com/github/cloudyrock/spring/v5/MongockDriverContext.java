package com.github.cloudyrock.spring.v5;

import com.github.cloudyrock.mongock.MongockConnectionDriver;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.SpringDataMongo2Driver;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.SpringDataMongo3Driver;
import io.changock.migration.api.exception.ChangockException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongockDriverContext {


  @Bean
  public MongockConnectionDriver mongockConnectionDriver(MongoTemplate mongoTemplate,
                                                         MongockConfiguration mongockConfiguration) {
    MongockConnectionDriver driver;
    try {
      driver = new SpringDataMongo3Driver(mongoTemplate);
    } catch (NoClassDefFoundError driver3NotFoundError) {
      try {
        driver = new SpringDataMongo2Driver(mongoTemplate);
        return driver;
      } catch (NoClassDefFoundError driver2NotFoundError) {
        throw new ChangockException("\n\n" + ConfigErrorMessages.DRIVER_NOT_FOUND_ERROR + "\n\n");
      }
    }
    driver.setLockSettings(mongockConfiguration.getLockAcquiredForMinutes(), mongockConfiguration.getMaxWaitingForLockMinutes(), mongockConfiguration.getMaxTries());
    driver.setChangeLogCollectionName(mongockConfiguration.getChangeLogCollectionName());
    driver.setLockCollectionName(mongockConfiguration.getLockCollectionName());
    driver.setIndexCreation(mongockConfiguration.isIndexCreation());
    driver.initialize();
    return driver;
  }

}
