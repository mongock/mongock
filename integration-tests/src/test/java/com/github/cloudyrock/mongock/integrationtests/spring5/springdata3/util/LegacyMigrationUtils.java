package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.util;

import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.changelogs.runalways.MongockSync4LegacyMigrationChangeRunAlwaysLog;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.changelogs.runonce.MongockSync4LegacyMigrationChangeLog;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class LegacyMigrationUtils {

    public static final String LEGACY_CHANGELOG_COLLECTION_NAME = "dbchangelog";

    private LegacyMigrationUtils() {
    }

    public static void setUpLegacyMigration(MongoCollection<Document> legacyChangeLogCollection) {
        legacyChangeLogCollection.insertOne(legacyMigrationChange1().append("timestamp", new Date()));
        legacyChangeLogCollection.insertOne(legacyMigrationChange2().append("timestamp", new Date()));

    }

    public static void setUpAlreadyMigratedLegacyMigrationChange(MongoCollection<Document> currentChangeLogCollection, boolean runAlways) {
        currentChangeLogCollection.insertOne(getResultingChangeFromExecutingLegacyMigration(runAlways));
        currentChangeLogCollection.insertOne(legacyMigrationChange1().append("state", "EXECUTED"));
        currentChangeLogCollection.insertOne(legacyMigrationChange2().append("state", "EXECUTED"));
    }

    public static void checkLegacyMigration(MongoCollection<Document> currentChangeLogCollection, boolean runAlways, int executions) {
        long change1FromLegacyCollectionCount = currentChangeLogCollection.countDocuments(legacyMigrationChange1().append("state", "EXECUTED"));
        assertEquals(1, change1FromLegacyCollectionCount);


        long change2FromLegacyCollectionCount = currentChangeLogCollection.countDocuments(legacyMigrationChange2().append("state", "EXECUTED"));
        assertEquals(1, change2FromLegacyCollectionCount);

        long resultingChangeFromExecutingLegacyMigrationCount = currentChangeLogCollection
                .countDocuments(getResultingChangeFromExecutingLegacyMigration(runAlways));
        assertEquals(executions, resultingChangeFromExecutingLegacyMigrationCount);

    }

    private static Document legacyMigrationChange1() {
        return new Document()
                .append("changeId", "01-addAuthorities")
                .append("author", "initiator")
                .append("changeLogClass", "io.cloudyrock.mongock.legacy.dbmigrations.InitialSetupMigration")
                .append("changeSetMethod", "addAuthorities");
    }

    private static Document legacyMigrationChange2() {
        return new Document()
                .append("changeId", "02-addUsers")
                .append("author", "initiator")
                .append("changeLogClass", "io.cloudyrock.mongock.legacy.dbmigrations.InitialSetupMigration")
                .append("changeSetMethod", "addUsers");
    }

    private static Document getResultingChangeFromExecutingLegacyMigration(boolean runAlways) {
        Class legacyMigrationClass = runAlways
                ? MongockSync4LegacyMigrationChangeRunAlwaysLog.class
                : MongockSync4LegacyMigrationChangeLog.class;
        return new Document()
                .append("changeId", "mongock-legacy-migration")
                .append("author", "mongock")
                .append("state", "EXECUTED")
                .append("changeLogClass", legacyMigrationClass.getName())
                .append("changeSetMethod", "mongockSpringLegacyMigration");
    }
}
