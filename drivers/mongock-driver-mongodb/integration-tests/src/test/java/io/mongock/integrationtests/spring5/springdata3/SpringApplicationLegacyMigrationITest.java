package io.mongock.integrationtests.spring5.springdata3;

import io.mongock.integrationtests.spring5.springdata3.changelogs.empty.EmptyChangeLog;
import io.mongock.integrationtests.spring5.springdata3.util.Constants;
import io.mongock.integrationtests.spring5.springdata3.util.LegacyMigrationUtils;
import io.mongock.integrationtests.spring5.springdata3.util.MongoContainer;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.awaitility.Awaitility.await;

// TODO add methodSources to automatize parametrization

@Testcontainers
class SpringApplicationLegacyMigrationITest {

  private ConfigurableApplicationContext ctx;

  @AfterEach
  void closingSpringApp() {
    if (ctx != null) {
      ctx.close();
      await().atMost(1, TimeUnit.MINUTES)
          .pollInterval(1, TimeUnit.SECONDS)
          .until(() -> !ctx.isActive());
    }
  }

  private void closeSpringApp() {
    if (ctx != null) {
      ctx.close();
      await().atMost(1, TimeUnit.MINUTES)
          .pollInterval(1, TimeUnit.SECONDS)
          .until(() -> !ctx.isActive());
    }
  }

  private static Stream<Arguments> provider() {
    return Stream.of(
        Arguments.of("mongo:4.2.6", false),
        Arguments.of("mongo:4.2.6", true)
    );
  }


  //TODO: test for legacyMigration
  // - should run normal with no run always
  // - should run normal with run always
  // - should not be tracked twice if executed twice but not run always
  // - should be tracked twice if executed twice and run always

  @ParameterizedTest
  @MethodSource("provider")
  void shouldRunLegacyMigration_IfNotHasRunYet(String mongoDbVersion, boolean sleuthEnabled) {
    MongoContainer mongoContainer = RuntimeTestUtil.startMongoDbContainer(mongoDbVersion);
    String replicaSetUrl = mongoContainer.getReplicaSetUrl();
    MongoDatabase database = MongoClients.create(replicaSetUrl).getDatabase(RuntimeTestUtil.DEFAULT_DATABASE_NAME);
    LegacyMigrationUtils.setUpLegacyMigration(database.getCollection(LegacyMigrationUtils.LEGACY_CHANGELOG_COLLECTION_NAME));
    try {
      Map<String, String> parameters = new HashMap<>();
      parameters.put("spring.sleuth.enabled", String.valueOf(sleuthEnabled));
      parameters.put("mongock.changeLogsScanPackage", EmptyChangeLog.class.getPackage().getName());
      parameters.put("mongock.legacy-migration.origin", LegacyMigrationUtils.LEGACY_CHANGELOG_COLLECTION_NAME);
      ctx = RuntimeTestUtil.startSpringAppWithParameters(mongoContainer, parameters);
    } catch (Exception ex) {
      //ignore
    }

    // then
    LegacyMigrationUtils.checkLegacyMigration(database.getCollection(Constants.CHANGELOG_COLLECTION_NAME), false, 1);
    closeSpringApp();
  }

  @ParameterizedTest
  @MethodSource("provider")
  void shouldNotRunLegacyMigration_IfNotRunAlways_WhenItIsAlreadyExecuted(String mongoDbVersion, boolean sleuthEnabled) {
    runLegacyMigrationWhenPreviouslyExecuted(mongoDbVersion, false, 1, sleuthEnabled);
  }

  @ParameterizedTest
  @MethodSource("provider")
  void shouldRunLegacyMigration_IfRunAlways_WhenItIsAlreadyExecuted(String mongoDbVersion, boolean sleuthEnabled) {
    runLegacyMigrationWhenPreviouslyExecuted(mongoDbVersion, true, 1, sleuthEnabled);
  }

  private void runLegacyMigrationWhenPreviouslyExecuted(String mongoDbVersion, boolean runAlways, int executions, boolean sleuthEnabled) {
    MongoContainer mongoContainer = RuntimeTestUtil.startMongoDbContainer(mongoDbVersion);
    String replicaSetUrl = mongoContainer.getReplicaSetUrl();
    MongoDatabase database = MongoClients.create(replicaSetUrl).getDatabase(RuntimeTestUtil.DEFAULT_DATABASE_NAME);
    MongoCollection<Document> currentChangeLogCollection = database.getCollection(Constants.CHANGELOG_COLLECTION_NAME);
    LegacyMigrationUtils.setUpAlreadyMigratedLegacyMigrationChange(currentChangeLogCollection, runAlways);
    LegacyMigrationUtils.setUpLegacyMigration(database.getCollection(LegacyMigrationUtils.LEGACY_CHANGELOG_COLLECTION_NAME));
    try {
      Map<String, String> parameters = new HashMap<>();
      parameters.put("spring.sleuth.enabled", String.valueOf(sleuthEnabled));
      parameters.put("mongock.changeLogsScanPackage", EmptyChangeLog.class.getPackage().getName());
      parameters.put("mongock.legacy-migration.origin", LegacyMigrationUtils.LEGACY_CHANGELOG_COLLECTION_NAME);
      parameters.put("mongock.legacy-migration.run-always", Boolean.toString(runAlways));
      ctx = RuntimeTestUtil.startSpringAppWithParameters(mongoContainer, parameters);
    } catch (Exception ex) {
      //ignore
    }

    // then
    LegacyMigrationUtils.checkLegacyMigration(currentChangeLogCollection, runAlways, executions);
    closeSpringApp();
  }

}
