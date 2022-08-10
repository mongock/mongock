package io.mongock.driver.mongodb.test.template.util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.mongock.driver.mongodb.test.template.extension.IntegrationTestSetupExtension;
import io.mongock.driver.mongodb.test.template.shared.IntegrationTestShared;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(IntegrationTestSetupExtension.class)
public abstract class IntegrationTestBase {

  protected static final String DEFAULT_DATABASE_NAME = "test_container";
  protected static final String CHANGELOG_COLLECTION_NAME = "mongockChangeLog";
  protected static final String LOCK_COLLECTION_NAME = "mongockLock";

  protected MongoDatabase getDataBase() {
    return IntegrationTestShared.getMongoDataBase();
  }

  protected MongoClient getMongoClient() {
    return IntegrationTestShared.getMongoClient();
  }

  protected MongoDBDriverTestAdapter getDefaultAdapter() {
    return getAdapter(CHANGELOG_COLLECTION_NAME);
  }

  protected abstract MongoDBDriverTestAdapter getAdapter(String collectionName);
}
