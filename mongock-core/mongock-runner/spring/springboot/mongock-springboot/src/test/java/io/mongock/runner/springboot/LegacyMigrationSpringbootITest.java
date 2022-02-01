package io.mongock.runner.springboot;

import com.github.silaev.mongodb.replicaset.MongoDbReplicaSet;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.mongock.api.config.LegacyMigration;
import io.mongock.api.config.TransactionStrategy;
import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.entry.ChangeState;
import io.mongock.runner.core.executor.MongockRunner;
import io.mongock.runner.springboot.domain.Client;
import io.mongock.runner.springboot.migration.EmptyChangeLog;
import io.mongock.runner.springboot.migration.FailingChangeLogLegacy;
import io.mongock.runner.springboot.migration.SpringDataAdvanceChangeLog;
import io.mongock.runner.springboot.migration.SpringDataAdvanceChangeLogWithBeforeFailing;
import io.mongock.runner.springboot.migration.SpringDataAdvanceChangeLogWithChangeSetFailing;
import io.mongock.runner.springboot.migration.TransactionSuccessfulChangeLog;
import io.mongock.runner.springboot.util.LegacyMigrationUtils;
import io.mongock.runner.springboot.util.RunnerTestUtil;
import io.mongock.util.test.Constants;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class LegacyMigrationSpringbootITest {

  private static MongoClient mongoClient;
  private static MongoDbReplicaSet mongodbContainer;
  private static RunnerTestUtil runnerTestUtil;
  private static MongoDatabase database;


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
    drop(legacyCollection);
    drop(changeEntryCollection);
    drop(database.getCollection(Constants.CHANGELOG_COLLECTION_NAME));
  }

  private void drop(MongoCollection<Document> legacyCollection) {
    if (legacyCollection != null) {
      legacyCollection.drop();
    }
  }

  @Test
  void shouldRunLegacyMigration_IfNotHasRunYet() {

    runRunnerWithLegacyMigration(1, false);

    // then
    changeEntryCollection = database.getCollection(Constants.CHANGELOG_COLLECTION_NAME);
    LegacyMigrationUtils.checkLegacyMigration(changeEntryCollection, false, 1);

  }

  @Test
  void shouldNotRunLegacyMigration_IfNotRunAlways_WhenItIsAlreadyExecuted() {
    runRunnerWithLegacyMigration(1, false);

    changeEntryCollection = database.getCollection(Constants.CHANGELOG_COLLECTION_NAME);
    LegacyMigrationUtils.checkLegacyMigration(changeEntryCollection, false, 1);

  }

  @Test
  void shouldRunLegacyMigration_IfRunAlways_WhenItIsAlreadyExecuted() {

    runRunnerWithLegacyMigration(1, true);

    changeEntryCollection = database.getCollection(Constants.CHANGELOG_COLLECTION_NAME);
    LegacyMigrationUtils.checkLegacyMigration(changeEntryCollection, true, 1);
  }


  private void runRunnerWithLegacyMigration(int executions, boolean runAlways) {
    legacyCollection = database.getCollection(LegacyMigrationUtils.LEGACY_CHANGELOG_COLLECTION_NAME);
    LegacyMigrationUtils.setUpLegacyMigration(legacyCollection);
    LegacyMigration legacyMigration = new LegacyMigration(LegacyMigrationUtils.LEGACY_CHANGELOG_COLLECTION_NAME);
    legacyMigration.setRunAlways(runAlways);

    for (int i = 0; i < executions; i++) {
      runnerTestUtil.getRunnerTransactional(EmptyChangeLog.class.getName())
          .setLegacyMigration(legacyMigration)
          .buildRunner()
          .execute();
    }
  }

}
