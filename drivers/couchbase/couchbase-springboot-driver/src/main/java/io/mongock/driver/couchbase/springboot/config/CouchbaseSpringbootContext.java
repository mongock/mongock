package io.mongock.driver.couchbase.springboot.config;

import com.couchbase.client.java.Collection;
import io.mongock.api.config.MongockConfiguration;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.couchbase.driver.CouchbaseDriver;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.data.couchbase.CouchbaseDataAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.CouchbaseClientFactory;
import org.springframework.util.StringUtils;


/**
 * {@link EnableAutoConfiguration Auto-configuration} for Mongock Couchbase support.
 *
 * @author Tigran Babloyan
 */
@Configuration
@ConditionalOnExpression("${mongock.enabled:true}")
@ConditionalOnBean({MongockConfiguration.class, CouchbaseClientFactory.class})
@EnableConfigurationProperties(CouchbaseConfiguration.class)
@AutoConfigureAfter(CouchbaseDataAutoConfiguration.class)
public class CouchbaseSpringbootContext {

  @Bean
  public ConnectionDriver connectionDriver(CouchbaseClientFactory couchbaseClientFactory,
                                           CouchbaseConfiguration couchbaseConfiguration,
                                           MongockConfiguration mongockConfig) {
    Collection collection = isCustomCollection(couchbaseConfiguration) ? 
        couchbaseClientFactory.withScope(couchbaseConfiguration.getScope()).getCollection(couchbaseConfiguration.getCollection()) : 
        couchbaseClientFactory.getDefaultCollection();  
    CouchbaseDriver driver = CouchbaseDriver.withLockStrategy(couchbaseClientFactory.getCluster(),
        collection,
        mongockConfig.getLockAcquiredForMillis(),
        mongockConfig.getLockQuitTryingAfterMillis(),
        mongockConfig.getLockTryFrequencyMillis());
    driver.setIndexCreation(mongockConfig.isIndexCreation());
    return driver;
  }
  
  private boolean isCustomCollection(CouchbaseConfiguration couchbaseConfiguration){
    return StringUtils.hasText(couchbaseConfiguration.getCollection())  &&
        StringUtils.hasText(couchbaseConfiguration.getScope());
  }
}
