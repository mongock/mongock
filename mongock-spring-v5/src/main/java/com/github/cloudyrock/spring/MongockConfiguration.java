package com.github.cloudyrock.spring;

import io.changock.runner.spring.v5.config.ChangockSpring5Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties("spring.mongock")
@ConditionalOnProperty(value = "spring.mongock.changeLogsScanPackage")
public class MongockConfiguration extends ChangockSpring5Configuration {
  private static final String DEFAULT_CHANGELOG_COLLECTION_NAME = "changockChangeLog";
  private final static String DEFAULT_LOCK_COLLECTION_NAME = "changockLock";
  /**
   *
   */
  private String changeLogCollectionName;

  /**
   *
   */
  private String lockCollectionName;
}
