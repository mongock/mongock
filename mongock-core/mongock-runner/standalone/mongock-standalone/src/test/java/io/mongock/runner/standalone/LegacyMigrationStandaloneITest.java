package io.mongock.runner.standalone;


import com.github.silaev.mongodb.replicaset.MongoDbReplicaSet;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.mongock.api.config.LegacyMigration;
import io.mongock.runner.standalone.migration.EmptyChangeLog;
import io.mongock.runner.standalone.util.LegacyMigrationUtils;
import io.mongock.runner.standalone.util.RunnerTestUtil;
import io.mongock.util.test.Constants;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class LegacyMigrationStandaloneITest {

  private static MongoClient mongoClient;
  private static MongoDatabase database;
  private static MongoDbReplicaSet mongodbContainer;
  private static RunnerTestUtil runnerTestUtil;

  private static MongoCollection<Document> changeEntryCollection;
  private static MongoCollection<Document> legacyCollection;

  @BeforeAll
  public static void startMongoDBForAllTests() {
    mongodbContainer = MongoDbReplicaSet.builder()
        .mongoDockerImageName("mongo:4.2.6")
//        .replicaSetNumber(2)
        .build();
    mongodbContainer.start();
    mongoClient = MongoClients.create(mongodbContainer.getReplicaSetUrl());
    database = mongoClient.getDatabase(Constants.DEFAULT_DATABASE_NAME);
    runnerTestUtil = new RunnerTestUtil(mongoClient);
  }

  @AfterAll
  public static void closeMongoDBForAllTests() {
    mongoClient.close();
    mongodbContainer.close();
  }

  @AfterEach
  public void cleanCommonDatabaseCollections() {
    drop(changeEntryCollection);
    drop(legacyCollection);
  }

  private static void drop(MongoCollection<Document> collection) {
    if (collection != null) {
      collection.drop();
    }
  }

  @Test
  void shouldPerformLegacyMigration() throws Exception {
    // given, then
    runRunnerWithLegacyMigration(1, false);

    // then
    changeEntryCollection = database.getCollection(Constants.CHANGELOG_COLLECTION_NAME);
    LegacyMigrationUtils.checkLegacyMigration(changeEntryCollection, false, 1);
  }


  @Test
  void shouldNotReapplyLegacyChangeLogs_IfNotRunAlways_WhenExecutedTwice() {
    // given, then
    runRunnerWithLegacyMigration(2, false);

    // then
    changeEntryCollection = database.getCollection(Constants.CHANGELOG_COLLECTION_NAME);
    LegacyMigrationUtils.checkLegacyMigration(changeEntryCollection, false, 1);

  }

  @Test
  void shouldNotDuplicateLegacyChangeLogs_IfRunAlways_WhenLegacyMigrationReapplied() {
    // given, then
    runRunnerWithLegacyMigration(2, true);

    // then
    changeEntryCollection = database.getCollection(Constants.CHANGELOG_COLLECTION_NAME);
    LegacyMigrationUtils.checkLegacyMigration(changeEntryCollection, true, 1);
  }


  private void runRunnerWithLegacyMigration(int executions, boolean runAlways) {
    legacyCollection = database.getCollection(LegacyMigrationUtils.LEGACY_CHANGELOG_COLLECTION_NAME);
    LegacyMigrationUtils.setUpLegacyMigration(legacyCollection);
    LegacyMigration legacyMigration = new LegacyMigration(LegacyMigrationUtils.LEGACY_CHANGELOG_COLLECTION_NAME);
    legacyMigration.setRunAlways(runAlways);

    for (int i = 0; i < executions; i++) {
      runnerTestUtil.getBuilder(true, EmptyChangeLog.class.getName())
          .setTransactionEnabled(true)
          .setLegacyMigration(legacyMigration)
          .buildRunner()
          .execute();
    }
  }
}
