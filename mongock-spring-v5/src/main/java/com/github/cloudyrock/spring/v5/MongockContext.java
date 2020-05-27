package com.github.cloudyrock.spring.v5;

import com.github.cloudyrock.mongock.MongockConnectionDriver;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.SpringDataMongo2Driver;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.SpringDataMongo3Driver;
import io.changock.migration.api.exception.ChangockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.StringUtils;

@Configuration
public class MongockContext {


  @Bean
  @ConditionalOnProperty(value = "spring.mongock.runner-type", matchIfMissing = true, havingValue = "ApplicationRunner")
  public MongockSpring5.MongockApplicationRunner mongockApplicationRunner(ApplicationContext springContext,
                                                                          MongoTemplate mongoTemplate,
                                                                          MongockConfiguration mongockConfiguration) {
    if(StringUtils.isEmpty(mongockConfiguration.getChangeLogsScanPackage())) {
      throw new ChangockException("\n\nMongock: You need to specify property: spring.mongock.changeLogsScanPackage\n\n");
    }
    return MongockSpring5.builder()
        .setDriver(getDriver(mongoTemplate, mongockConfiguration))
        .addChangeLogsScanPackage(mongockConfiguration.getChangeLogsScanPackage())
        .setSpringContext(springContext)
        .setLockConfig(mongockConfiguration.getLockAcquiredForMinutes(), mongockConfiguration.getMaxWaitingForLockMinutes(), mongockConfiguration.getMaxTries())//optional
        .buildApplicationRunner();


  }

  @Bean
  @ConditionalOnProperty(value = "spring.mongock.runner-type", havingValue = "InitializingBean")
  public MongockSpring5.MongockInitializingBeanRunner mongockInitializingBeanRunner(ApplicationContext springContext,
                                                                                    MongoTemplate mongoTemplate,
                                                                                    MongockConfiguration mongockConfiguration) {
    if(StringUtils.isEmpty(mongockConfiguration.getChangeLogsScanPackage())) {
      throw new ChangockException("\n\nMongock: You need to specify property: spring.mongock.changeLogsScanPackage\n\n");
    }
    return MongockSpring5.builder()
        .setDriver(getDriver(mongoTemplate, mongockConfiguration))
        .addChangeLogsScanPackage(mongockConfiguration.getChangeLogsScanPackage())
        .setLockConfig(mongockConfiguration.getLockAcquiredForMinutes(), mongockConfiguration.getMaxWaitingForLockMinutes(), mongockConfiguration.getMaxTries())//optional
        .setSpringContext(springContext)
        .buildInitializingBeanRunner();
  }


  private MongockConnectionDriver getDriver(MongoTemplate mongoTemplate, MongockConfiguration mongockConfiguration) {
    try {
      SpringDataMongo3Driver driver = new SpringDataMongo3Driver(mongoTemplate);
      driver.setChangeLogCollectionName(mongockConfiguration.getChangeLogCollectionName());
      driver.setLockCollectionName(mongockConfiguration.getLockCollectionName());
      return driver;
    } catch (NoClassDefFoundError driver3NotFoundError) {
      try {
        SpringDataMongo2Driver driver = new SpringDataMongo2Driver(mongoTemplate);
        driver.setChangeLogCollectionName(mongockConfiguration.getChangeLogCollectionName());
        driver.setLockCollectionName(mongockConfiguration.getLockCollectionName());
        return driver;
      } catch (NoClassDefFoundError driver2NotFoundError) {
        throw new ChangockException("\n\n" + DRIVER_NOT_FOUND_ERROR + "\n\n");
      }
    }
  }

  private final static String DRIVER_NOT_FOUND_ERROR = "MONGOCK DRIVER HAS NOT BEEN IMPORTED" +
      "\n====================================" +
      "\n\tSOLUTION: You need to import one of these artifacts" +
      "\n\t- 'mongodb-springdata-v3-driver' for springdata 3" +
      "\n\t- 'mongodb-springdata-v2-driver' for springdata 2";

}
