package com.github.cloudyrock.mongock.samples.v4.spring5.springdata3;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.SpringDataMongo3Driver;
import com.github.cloudyrock.mongock.samples.v4.spring5.springdata3.changelogs.ClientChangeLog;
import com.github.cloudyrock.mongock.samples.v4.spring5.springdata3.client.ClientRepository;
import com.github.cloudyrock.mongock.samples.v4.spring5.springdata3.spring.DateToZonedDateTimeConverter;
import com.github.cloudyrock.mongock.samples.v4.spring5.springdata3.spring.ZonedDateTimeToDateConverter;
import com.github.cloudyrock.spring.MongockSpring5;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = ClientRepository.class)
public class Mongock4Spring5SpringData3App {

  public final static String CLIENTS_COLLECTION_NAME = "clientCollection";

  public static void main(String[] args) {
    SpringApplication.run(Mongock4Spring5SpringData3App.class, args);
  }


  @Bean
  @Profile("!initializingBean")
  public MongockSpring5.MongockApplicationRunner mongockApplicationRunner(ApplicationContext springContext,
                                                                          MongoTemplate mongoTemplate,
                                                                          @Value("${mongock.lock.lockAcquiredForMinutes:5}") long lockAcquiredForMinutes,
                                                                          @Value("${mongock.lock.maxWaitingForLockMinutes:3}") long maxWaitingForLockMinutes,
                                                                          @Value("${mongock.lock.maxTries:3}") int maxTries) {
    SpringDataMongo3Driver driver = new SpringDataMongo3Driver(mongoTemplate);
    driver.setLockSettings(lockAcquiredForMinutes, maxWaitingForLockMinutes, maxTries);//optional
    driver.setChangeLogCollectionName("new_changelog_collection_name");//optional
    driver.setLockCollectionName("new_lock_collection_name");//optional
    return MongockSpring5.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(ClientChangeLog.class.getPackage().getName())
        .setSpringContext(springContext)
        .buildApplicationRunner();
  }

  @Bean
  @Profile("initializingBean")
  public MongockSpring5.MongockInitializingBeanRunner mongockInitializingBeanRunner(ApplicationContext springContext,
                                                                                    MongoTemplate mongoTemplate,
                                                                                    @Value("${mongock.lock.lockAcquiredForMinutes:5}") long lockAcquiredForMinutes,
                                                                                    @Value("${mongock.lock.maxWaitingForLockMinutes:3}") long maxWaitingForLockMinutes,
                                                                                    @Value("${mongock.lock.maxTries:3}") int maxTries) {
    SpringDataMongo3Driver driver = new SpringDataMongo3Driver(mongoTemplate);
    driver.setLockSettings(lockAcquiredForMinutes, maxWaitingForLockMinutes, maxTries);//optional
    driver.setChangeLogCollectionName("new_changelog_collection_name");//optional
    driver.setLockCollectionName("new_lock_collection_name");//optional
    return MongockSpring5.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(ClientChangeLog.class.getPackage().getName())
        .setSpringContext(springContext)
        .buildInitializingBeanRunner();
  }


  @Bean
  public MongoCustomConversions customConversions() {
    List<Converter<?, ?>> converters = new ArrayList<>();
    converters.add(DateToZonedDateTimeConverter.INSTANCE);
    converters.add(ZonedDateTimeToDateConverter.INSTANCE);
    return new MongoCustomConversions(converters);
  }

}
