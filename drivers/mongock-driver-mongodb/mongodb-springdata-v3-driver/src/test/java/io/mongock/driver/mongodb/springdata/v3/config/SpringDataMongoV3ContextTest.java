/*
 * Copyright © Ericsson AB 2021.
 *
 * All Rights Reserved.
 *
 * Reproduction in whole or in part is prohibited without the written consent of the copyright owner.
 *
 */

package io.mongock.driver.mongodb.springdata.v3.config;

import com.mongodb.client.MongoClients;
import io.mongock.api.config.MongockConfiguration;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.mongodb.springdata.v3.SpringDataMongoV3Driver;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.sleuth.autoconfig.brave.BraveAutoConfiguration;
import org.springframework.cloud.sleuth.autoconfig.instrument.config.TraceSpringCloudConfigAutoConfiguration;
import org.springframework.cloud.sleuth.autoconfig.instrument.mongodb.TraceMongoDbAutoConfiguration;
import org.springframework.cloud.sleuth.autoconfig.instrument.tx.TraceTxAutoConfiguration;
import org.springframework.cloud.sleuth.instrument.tx.TracePlatformTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class SpringDataMongoV3ContextTest {
  @Test
  public void withoutSleuth() {
    ApplicationContextRunner applicationContextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(
            MongockConfiguration.class,
            MongoTxConfiguration.class,
            SpringDataMongoV3Context.class,
            MongoAutoConfiguration.class,
            MongoDataAutoConfiguration.class));
    applicationContextRunner.run((context) -> assertThat(context)
        .hasSingleBean(MongoTransactionManager.class)
        .hasSingleBean(MongoTemplate.class)
        .hasSingleBean(ConnectionDriver.class));
  }

  @Test
  public void withSleuth() {
    ApplicationContextRunner applicationContextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(
            MongockConfiguration.class,
            MongoTxConfiguration.class,
            SpringDataMongoV3Context.class,
            MongoAutoConfiguration.class,
            MongoDataAutoConfiguration.class,
            TraceMongoDbAutoConfiguration.class,
            TraceSpringCloudConfigAutoConfiguration.class,
            BraveAutoConfiguration.class,
            TraceTxAutoConfiguration.class));
    applicationContextRunner.run((context) -> assertThat(context)
        .hasSingleBean(TracePlatformTransactionManager.class)
        .hasSingleBean(MongoTemplate.class)
        .hasSingleBean(ConnectionDriver.class));
  }

  @Configuration(proxyBeanMethods = false)
  static class MongoTxConfiguration {
    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
      return new MongoTransactionManager(dbFactory);
    }
  }
}
