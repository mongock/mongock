package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3;

import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.general.AnotherMongockTestResource;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.general.MongockTestResource;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.withChangockAnnotations.ChangeLogwithChangockAnnotations;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.util.Constants;
import com.github.cloudyrock.springboot.v2_2.MongockSpringbootV2_4;
import org.bson.Document;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class RunnerITest extends ApplicationRunnerTestBase{

    private static final String TEST_RESOURCE_CLASSPATH = MongockTestResource.class.getPackage().getName();

    @ParameterizedTest
    @ValueSource(strings = {"mongo:4.2.6"})
    void shouldExecuteAllChangeSets(String mongoVersion) throws Exception {
        start(mongoVersion);
        // given, then
        getBasicBuilder(TEST_RESOURCE_CLASSPATH).buildApplicationRunner().run(null);

        // db changelog collection checking
        long change1 = this.mongoTemplate.getDb().getCollection(Constants.CHANGELOG_COLLECTION_NAME)
                .countDocuments(new Document().append("changeId", "test1").append("author", "testuser"));
        assertEquals(1, change1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"mongo:4.2.6"})
    void shouldStoreMetadata_WhenChangeSetIsTrack_IfAddedInBuilder(String mongoVersion) throws Exception {
        start(mongoVersion);
        // given
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("string_key", "string_value");
        metadata.put("integer_key", 10);
        metadata.put("float_key", 11.11F);
        metadata.put("double_key", 12.12D);
        metadata.put("long_key", 13L);
        metadata.put("boolean_key", true);

        // then
        getBasicBuilder(TEST_RESOURCE_CLASSPATH)
                .withMetadata(metadata)
                .buildApplicationRunner()
                .run(null);

        // then
        Map metadataResult = mongoTemplate.getDb().getCollection(Constants.CHANGELOG_COLLECTION_NAME).find().first().get("metadata", Map.class);
        assertEquals("string_value", metadataResult.get("string_key"));
        assertEquals(10, metadataResult.get("integer_key"));
        assertEquals(11.11F, (Double) metadataResult.get("float_key"), 0.01);
        assertEquals(12.12D, (Double) metadataResult.get("double_key"), 0.01);
        assertEquals(13L, metadataResult.get("long_key"));
        assertEquals(true, metadataResult.get("boolean_key"));

    }

    @ParameterizedTest
    @ValueSource(strings = {"mongo:4.2.6"})
    void shouldTwoExecutedChangeSet_whenRunningTwice_ifRunAlways(String mongoVersion) throws Exception {
        start(mongoVersion);
        // given
        MongockSpringbootV2_4.MongockApplicationRunner runner = getBasicBuilder(TEST_RESOURCE_CLASSPATH).buildApplicationRunner();

        // when
        runner.run(null);
        runner.run(null);

        // then
        List<Document> documentList = new ArrayList<>();

        mongoTemplate.getDb().getCollection(Constants.CHANGELOG_COLLECTION_NAME)
                .find(new Document().append("changeSetMethod", "testChangeSetWithAlways").append("state", "EXECUTED"))
                .forEach(documentList::add);

        assertEquals(1, documentList.size());

    }

    @ParameterizedTest
    @ValueSource(strings = {"mongo:4.2.6"})
    void shouldOneExecutedAndOneIgnoredChangeSet_whenRunningTwice_ifNotRunAlwaysAndTrackIgnore(String mongoVersion) throws Exception {
        start(mongoVersion);
        // given
        MongockSpringbootV2_4.MongockApplicationRunner runner = getBasicBuilder(TEST_RESOURCE_CLASSPATH)
                .setTrackIgnored(true)
                .buildApplicationRunner();


        // when
        runner.run(null);
        runner.run(null);

        // then
        List<String> stateList = new ArrayList<>();
        mongoTemplate.getDb().getCollection(Constants.CHANGELOG_COLLECTION_NAME)
                .find(new Document()
                        .append("changeLogClass", AnotherMongockTestResource.class.getName())
                        .append("changeSetMethod", "testChangeSet"))
                .map(document -> document.getString("state"))
                .forEach(stateList::add);
        assertEquals(2, stateList.size());
        assertTrue(stateList.contains("EXECUTED"));
        assertTrue(stateList.contains("IGNORED"));
    }


    @ParameterizedTest
    @ValueSource(strings = {"mongo:4.2.6"})
    void shouldOneExecutedAndNoIgnoredChangeSet_whenRunningTwice_ifNotRunAlwaysAndNotTrackIgnore(String mongoVersion) throws Exception {
        start(mongoVersion);
        // given
        MongockSpringbootV2_4.MongockApplicationRunner runner = getBasicBuilder(TEST_RESOURCE_CLASSPATH)
                .buildApplicationRunner();


        // when
        runner.run(null);
        runner.run(null);

        // then
        List<String> stateList = new ArrayList<>();
        mongoTemplate.getDb().getCollection(Constants.CHANGELOG_COLLECTION_NAME)
                .find(new Document()
                        .append("changeLogClass", AnotherMongockTestResource.class.getName())
                        .append("changeSetMethod", "testChangeSet"))
                .map(document -> document.getString("state"))
                .forEach(stateList::add);
        assertEquals(1, stateList.size());
        assertTrue(stateList.contains("EXECUTED"));
        assertFalse(stateList.contains("IGNORED"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"mongo:4.2.6"})
    void shouldExecuteChangockAnnotations(String mongoVersion) throws Exception {
        start(mongoVersion);
        // given, then
        getBasicBuilder(ChangeLogwithChangockAnnotations.class.getPackage().getName()).buildApplicationRunner().run(null);

        // then
        long changeWithChangockAnnotations = mongoTemplate.getDb().getCollection(Constants.CHANGELOG_COLLECTION_NAME).countDocuments(new Document()
                .append("changeId", "withChangockAnnotations")
                .append("author", "testuser")
                .append("state", "EXECUTED"));
        assertEquals(1, changeWithChangockAnnotations);
    }

}
