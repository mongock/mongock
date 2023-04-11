package io.mongock.driver.couchbase.springboot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for Mongock Couchbase.
 *
 * @author Tigran Babloyan
 */
@Configuration
@ConfigurationProperties("mongock.couchbase")
public class CouchbaseConfiguration {
  /**
   * The custom scope to be used by the Mongock.
   * Can be used for Couchbase Server 7+ to set custom scope on the stored data.
   * 
   * Note: If scope is set the collection needs to be set as well.
   */
  private String scope;
  /**
   * The custom collection to be used by the Mongock.
   * Can be used for Couchbase Server 7+ to set custom collection on the stored data.
   */
  private String collection;

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public String getCollection() {
    return collection;
  }

  public void setCollection(String collection) {
    this.collection = collection;
  }
}
