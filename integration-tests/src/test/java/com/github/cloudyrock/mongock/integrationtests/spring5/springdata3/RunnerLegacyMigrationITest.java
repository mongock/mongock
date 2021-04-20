package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3;

import com.github.cloudyrock.mongock.config.LegacyMigration;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.empty.EmptyChangeLog;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.util.Constants;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.util.LegacyMigrationUtils;
import com.github.cloudyrock.springboot.v2_2.MongockSpringbootV2_4;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class RunnerLegacyMigrationITest extends ApplicationRunnerTestBase {

    @ParameterizedTest
    @ValueSource(strings = {"mongo:4.2.6"})
    void shouldPerformLegacyMigration(String mongoVersion) throws Exception {
        start(mongoVersion);
        // given, then
        runRunnerWithLegacyMigration(1, false);

        // then
        LegacyMigrationUtils.checkLegacyMigration(mongoTemplate.getDb().getCollection(Constants.CHANGELOG_COLLECTION_NAME), false, 1);

    }


    @ParameterizedTest
    @ValueSource(strings = {"mongo:4.2.6"})
    void shouldNotReapplyLegacyChangeLogs_IfNotRunAlways_WhenExecutedTwice(String mongoVersion) throws Exception {
        start(mongoVersion);
        // given, then
        runRunnerWithLegacyMigration(2, false);

        // then
        LegacyMigrationUtils.checkLegacyMigration(mongoTemplate.getDb().getCollection(Constants.CHANGELOG_COLLECTION_NAME), false, 1);

    }

    @ParameterizedTest
    @ValueSource(strings = {"mongo:4.2.6"})
    void shouldNotDuplicateLegacyChangeLogs_IfRunAlways_WhenLegacyMigrationReapplied(String mongoVersion) throws Exception {
        start(mongoVersion);
        // given, then
        runRunnerWithLegacyMigration(2, true);

        // then
        LegacyMigrationUtils.checkLegacyMigration(mongoTemplate.getDb().getCollection(Constants.CHANGELOG_COLLECTION_NAME), true, 1);
    }

    private void runRunnerWithLegacyMigration(int executions, boolean runAlways) throws Exception {
        MongoCollection<Document> collection = mongoTemplate.getCollection(LegacyMigrationUtils.LEGACY_CHANGELOG_COLLECTION_NAME);
        LegacyMigrationUtils.setUpLegacyMigration(collection);
//        String packageName = runAlways
//                ? MongockSync4LegacyMigrationChangeRunAlwaysLog.class.getPackage().getName()
//                : MongockSync4LegacyMigrationChangeLog.class.getPackage().getName();
        LegacyMigration legacyMigration = new LegacyMigration(LegacyMigrationUtils.LEGACY_CHANGELOG_COLLECTION_NAME);
        legacyMigration.setRunAlways(runAlways);
        MongockSpringbootV2_4.MongockApplicationRunner runner = getBasicBuilder(EmptyChangeLog.class.getPackage().getName())
                .setLegacyMigration(legacyMigration)
                .buildApplicationRunner();

        for (int i = 0; i < executions; i++) {
            runner.run(null);
        }
    }

}

