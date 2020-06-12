package com.github.cloudyrock.spring.v5;

import com.github.cloudyrock.mongock.MongockConnectionDriver;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.SpringDataMongo2Driver;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.SpringDataMongo3Driver;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.changelogs.MongockSync4LegacyMigrationChangeLog;
import com.github.cloudyrock.mongock.driver.mongodb.v3.changelogs.MongockV3LegacyMigrationChangeLog;
import io.changock.migration.api.exception.ChangockException;
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
    return builder(springContext, mongoTemplate, mongockConfiguration).buildApplicationRunner();
  }

  @Bean
  @ConditionalOnProperty(value = "spring.mongock.runner-type", havingValue = "InitializingBean")
  public MongockSpring5.MongockInitializingBeanRunner mongockInitializingBeanRunner(ApplicationContext springContext,
                                                                                    MongoTemplate mongoTemplate,
                                                                                    MongockConfiguration mongockConfiguration) {
    return builder(springContext, mongoTemplate, mongockConfiguration).buildInitializingBeanRunner();
  }

  private MongockSpring5.Builder builder(ApplicationContext springContext, MongoTemplate mongoTemplate, MongockConfiguration mongockConfiguration) {
    if (StringUtils.isEmpty(mongockConfiguration.getChangeLogsScanPackage())) {
      throw new ChangockException("\n\nMongock: You need to specify property: spring.mongock.changeLogsScanPackage\n\n");
    }
    MongockSpring5.Builder builder = MongockSpring5.builder()
        .setDriver(getDriver(mongoTemplate, mongockConfiguration))
        .setConfig(mongockConfiguration)
        .setSpringContext(springContext);
    setLegacyMigrationChangeLog(builder, mongockConfiguration);
    return builder;
  }

  private void setLegacyMigrationChangeLog(MongockSpring5.Builder builder, MongockConfiguration mongockConfiguration) {
    if(mongockConfiguration.getLegacyMigration() != null) {
      try {
        builder.addChangeLogsScanPackage(MongockSync4LegacyMigrationChangeLog.class.getPackage().getName());
      } catch (NoClassDefFoundError mongockSyncDriverNotFoundError) {
        try {
          builder.addChangeLogsScanPackage(MongockV3LegacyMigrationChangeLog.class.getPackage().getName());
        } catch (NoClassDefFoundError mongockDriverV3NotFoundError) {
          throw new ChangockException("\n\n" + DRIVER_NOT_FOUND_ERROR + "\n\n");
        }
      }
    }
  }

  private MongockConnectionDriver getDriver(MongoTemplate mongoTemplate, MongockConfiguration mongockConfiguration) {
    MongockConnectionDriver driver;
    try {
      driver = new SpringDataMongo3Driver(mongoTemplate);
    } catch (NoClassDefFoundError driver3NotFoundError) {
      try {
        driver = new SpringDataMongo2Driver(mongoTemplate);
        return driver;
      } catch (NoClassDefFoundError driver2NotFoundError) {
        throw new ChangockException("\n\n" + DRIVER_NOT_FOUND_ERROR + "\n\n");
      }
    }
    driver.setChangeLogCollectionName(mongockConfiguration.getChangeLogCollectionName());
    driver.setLockCollectionName(mongockConfiguration.getLockCollectionName());
    driver.setIndexCreation(mongockConfiguration.isIndexCreation());
    return driver;
  }

  private final static String DRIVER_NOT_FOUND_ERROR = "MONGOCK DRIVER HAS NOT BEEN IMPORTED" +
      "\n====================================" +
      "\n\tSOLUTION: You need to import one of these artifacts" +
      "\n\t- 'mongodb-springdata-v3-driver' for springdata 3" +
      "\n\t- 'mongodb-springdata-v2-driver' for springdata 2";

}
