package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.test.proxy.ProxiesMongockTestResource;
import com.mongodb.DB;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.jongo.Jongo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.verification.Times;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JongoMongockTest extends JongoMongockTestBase {

  @Test
  public void shouldExecuteAllChangeSets() throws Exception {
    // given
    when(changeEntryRepository.isNewChange(any(ChangeEntry.class))).thenReturn(true);

    // when
    runner.execute();

    // then
    verify(changeEntryRepository, times(4)).save(any(ChangeEntry.class)); // 4 changesets saved to dbchangelog

    // dbchangelog collection checking
    long change1 = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document()
        .append(ChangeEntry.KEY_CHANGEID, "test1")
        .append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(1, change1);
    long change2 = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document()
        .append(ChangeEntry.KEY_CHANGEID, "test2")
        .append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(1, change2);
    long change3 = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document()
        .append(ChangeEntry.KEY_CHANGEID, "test3")
        .append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(1, change3);
    long change4 = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document()
        .append(ChangeEntry.KEY_CHANGEID, "test4")
        .append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(1, change4);

    long changeAll = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document()
        .append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(4, changeAll);
  }

  @Test
  public void shouldUsePreConfiguredJongo() throws Exception {
    TestUtils.setField(runner, "jongo", null);
    Jongo jongo = mock(Jongo.class);
    runner.setJongo(jongo);
    when(jongo.getDatabase()).thenReturn(null);
    TestUtils.setField(runner, "jongo", jongo);
    runner.execute();
    verify(jongo).getDatabase();
  }

  @Test
  public void shouldInjectProxyToChangeEntry() throws Exception {

    com.github.cloudyrock.mongock.test.proxy.ProxiesMongockTestResource changeLog = mock(com.github.cloudyrock.mongock.test.proxy.ProxiesMongockTestResource.class);

    when(changeEntryRepository.isNewChange(any(ChangeEntry.class))).thenReturn(true);
    doReturn(Collections.singletonList(com.github.cloudyrock.mongock.test.proxy.ProxiesMongockTestResource.class))
        .when(changeService).fetchChangeLogs();
    doReturn(changeLog).when(changeService).createInstance(any(Class.class));
    doReturn(Arrays.asList(
        com.github.cloudyrock.mongock.test.proxy.ProxiesMongockTestResource.class.getDeclaredMethod("testInsertWithDB", DB.class),
        com.github.cloudyrock.mongock.test.proxy.ProxiesMongockTestResource.class.getDeclaredMethod("testJongo", Jongo.class),
        ProxiesMongockTestResource.class.getDeclaredMethod("testMongoDatabase", MongoDatabase.class)))
        .when(changeService).fetchChangeSets(any(Class.class));

    when(changeEntryRepository.isNewChange(any(ChangeEntry.class))).thenReturn(true);
    // given
    when(changeEntryRepository.isNewChange(any(ChangeEntry.class))).thenReturn(true);
    runner.setChangelogMongoDatabase(fakeMongoDatabase);

    DB proxyDb = mock(DB.class);
    runner.setChangelogDb(proxyDb);

    Jongo proxyJongo = mock(Jongo.class);
    runner.setJongo(proxyJongo);

    MongoDatabase proxyMongoDatabase = mock(MongoDatabase.class);
    runner.setChangelogMongoDatabase(proxyMongoDatabase);

    // when
    runner.execute();

    //then
    verify(changeLog, new Times(1)).testInsertWithDB(proxyDb);
    verify(changeLog, new Times(1)).testJongo(proxyJongo);
    verify(changeLog, new Times(1)).testMongoDatabase(proxyMongoDatabase);
  }

}
