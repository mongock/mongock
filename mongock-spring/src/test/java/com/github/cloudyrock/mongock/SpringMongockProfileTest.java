package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.resources.EnvironmentMock;
import com.github.cloudyrock.mongock.test.changelogs.AnotherMongockTestResource;
import com.github.cloudyrock.mongock.test.profiles.def.UnProfiledChangeLog;
import com.github.cloudyrock.mongock.test.profiles.dev.ProfiledDevChangeLog;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Tests for Spring profiles integration
 *
 *
 * @since 2014-09-17
 */
@RunWith(MockitoJUnitRunner.class)
public class SpringMongockProfileTest extends SpringMongockTestBase {
  private static final int CHANGELOG_COUNT = 11;

  @Test
  public void shouldRunDevProfileAndNonAnnotated() throws Exception {
    // given
    changeService.setEnvironment(new EnvironmentMock("dev", "test"));
    changeService.setChangeLogsBasePackage(ProfiledDevChangeLog.class.getPackage().getName());
    when(changeEntryRepository.isNewChange(any(ChangeEntry.class))).thenReturn(true);

    // when
    runner.execute();

    long count = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count();

    // then
    long change1 = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME)
        .count(new Document()
            .append(ChangeEntry.KEY_CHANGEID, "Pdev1")
            .append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(1, change1);  //  no-@Profile  should not match

    long change2 = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME)
        .count(new Document()
            .append(ChangeEntry.KEY_CHANGEID, "Pdev4")
            .append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(1, change2);  //  @Profile("dev")  should not match

    long change3 = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME)
        .count(new Document()
            .append(ChangeEntry.KEY_CHANGEID, "Pdev3")
            .append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(0, change3);  //  @Profile("default")  should not match
  }

  @Test
  public void shouldRunUnprofiledChangeLog() throws Exception {
    // given
    changeService.setChangeLogsBasePackage(UnProfiledChangeLog.class.getPackage().getName());
    TestUtils.setField(runner, "springEnvironment", new EnvironmentMock("test"));
    when(changeEntryRepository.isNewChange(any(ChangeEntry.class))).thenReturn(true);

    // when
    runner.execute();

    // then
    long change1 = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME)
        .count(new Document()
            .append(ChangeEntry.KEY_CHANGEID, "Pdev1")
            .append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(1, change1);

    long change2 = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME)
        .count(new Document()
            .append(ChangeEntry.KEY_CHANGEID, "Pdev2")
            .append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(1, change2);

    long change3 = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME)
        .count(new Document()
            .append(ChangeEntry.KEY_CHANGEID, "Pdev3")
            .append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(1, change3);  //  @Profile("dev")  should not match

    long change4 = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME)
        .count(new Document()
            .append(ChangeEntry.KEY_CHANGEID, "Pdev4")
            .append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(0, change4);  //  @Profile("pro")  should not match

    long change5 = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME)
        .count(new Document()
            .append(ChangeEntry.KEY_CHANGEID, "Pdev5")
            .append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(1, change5);  //  @Profile("!pro")  should match
  }

  @Test
  public void shouldNotRunAnyChangeSet() throws Exception {
    // given
    changeService.setChangeLogsBasePackage(ProfiledDevChangeLog.class.getPackage().getName());
    TestUtils.setField(runner, "springEnvironment", new EnvironmentMock("foobar"));
    when(changeEntryRepository.isNewChange(any(ChangeEntry.class))).thenReturn(true);

    // when
    runner.execute();

    // then
    long changes = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document());
    assertEquals(0, changes);
  }

  @Test
  public void shouldRunChangeSetsWhenNoEnv() throws Exception {
    // given
    changeService.setChangeLogsBasePackage(AnotherMongockTestResource.class.getPackage().getName());
    TestUtils.setField(runner, "springEnvironment", null);
    when(changeEntryRepository.isNewChange(any(ChangeEntry.class))).thenReturn(true);

    // when
    runner.execute();

    // then
    long changes = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document());
    assertEquals(CHANGELOG_COUNT, changes);
  }

  @Test
  public void shouldRunChangeSetsWhenEmptyEnv() throws Exception {
    // given
    changeService.setChangeLogsBasePackage(AnotherMongockTestResource.class.getPackage().getName());
    TestUtils.setField(runner, "springEnvironment", new EnvironmentMock());
    when(changeEntryRepository.isNewChange(any(ChangeEntry.class))).thenReturn(true);

    // when
    runner.execute();

    // then
    long changes = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document());
    assertEquals(CHANGELOG_COUNT, changes);
  }

  @Test
  public void shouldRunAllChangeSets() throws Exception {
    // given
    changeService.setChangeLogsBasePackage(AnotherMongockTestResource.class.getPackage().getName());
    TestUtils.setField(runner, "springEnvironment", new EnvironmentMock("dev"));
    when(changeEntryRepository.isNewChange(any(ChangeEntry.class))).thenReturn(true);

    // when
    runner.execute();

    // then
    long changes = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document());
    assertEquals(CHANGELOG_COUNT, changes);
  }

}
