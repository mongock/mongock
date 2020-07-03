package com.github.cloudyrock.spring.v5;

import com.github.cloudyrock.mongock.MongockConnectionDriver;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.changelogs.MongockSync4LegacyMigrationChangeLog;
import com.github.cloudyrock.mongock.driver.mongodb.v3.changelogs.MongockV3LegacyMigrationChangeLog;
import io.changock.migration.api.exception.ChangockException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

@Configuration
@Import(MongockDriverContext.class)
public class MongockContext {


  @Bean
  @ConditionalOnProperty(value = "spring.mongock.runner-type", matchIfMissing = true, havingValue = "ApplicationRunner")
  public MongockSpring5.MongockApplicationRunner mongockApplicationRunner(MongockConnectionDriver mongockConnectionDriver,
                                                                          MongockConfiguration mongockConfiguration,
                                                                          ApplicationContext springContext) {
    return builder(mongockConnectionDriver, mongockConfiguration, springContext).buildApplicationRunner();
  }

  @Bean
  @ConditionalOnProperty(value = "spring.mongock.runner-type", havingValue = "InitializingBean")
  public MongockSpring5.MongockInitializingBeanRunner mongockInitializingBeanRunner(MongockConnectionDriver mongockConnectionDriver,
                                                                                    MongockConfiguration mongockConfiguration,
                                                                                    ApplicationContext springContext) {
    return builder(mongockConnectionDriver, mongockConfiguration, springContext).buildInitializingBeanRunner();
  }

  private MongockSpring5.Builder builder(MongockConnectionDriver mongockConnectionDriver,
                                         MongockConfiguration mongockConfiguration,
                                         ApplicationContext springContext) {
    if (StringUtils.isEmpty(mongockConfiguration.getChangeLogsScanPackage())) {
      throw new ChangockException("\n\nMongock: You need to specify property: spring.mongock.changeLogsScanPackage\n\n");
    }
    MongockSpring5.Builder builder = MongockSpring5.builder()
        .setDriver(mongockConnectionDriver)
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
          throw new ChangockException("\n\n" + ConfigErrorMessages.DRIVER_NOT_FOUND_ERROR + "\n\n");
        }
      }
    }
  }


}
