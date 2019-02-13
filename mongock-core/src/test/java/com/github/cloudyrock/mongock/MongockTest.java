package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.test.changelogs.MongockTestResource;
import com.github.cloudyrock.mongock.test.changelogs.MongockTestResourceWithField;
import com.github.cloudyrock.mongock.test.proxy.ProxiesMongockTestResource;
import com.google.common.collect.Sets;
import com.mongodb.DB;
import com.mongodb.client.MongoDatabase;
import junit.framework.Assert;
import org.bson.Document;
import org.hamcrest.BaseMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsEqual;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Equals;
import org.mockito.internal.verification.Times;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MongockTest extends MongockTestBase {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void shouldExecuteAllChangeSets() throws Exception {
    // given
    when(changeEntryRepository.isNewChange(any(ChangeEntry.class))).thenReturn(true);

    // when
    runner.execute();

    // then
    verify(changeEntryRepository, times(12)).save(any(ChangeEntry.class)); // 11 changesets saved to dbchangelog

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
    long change5 = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document()
        .append(ChangeEntry.KEY_CHANGEID, "test5")
        .append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(1, change5);

    long changeAll = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document()
        .append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(12, changeAll);
  }

  @Test
  public void shouldPassOverChangeSets() throws Exception {
    // given
    when(changeEntryRepository.isNewChange(any(ChangeEntry.class))).thenReturn(false);

    // when
    runner.execute();

    // then
    verify(changeEntryRepository, times(0)).save(any(ChangeEntry.class)); // no changesets saved to dbchangelog
  }

  @Test
  public void shouldExecuteProcessWhenLockAcquired() throws Exception {

    // when
    runner.execute();

    // then
    verify(changeEntryRepository, atLeastOnce()).isNewChange(any(ChangeEntry.class));
  }

  @Test
  public void shouldReleaseLockAfterWhenLockAcquired() throws Exception {
    // when
    runner.execute();

    // then
    verify(lockChecker).releaseLockDefault();
  }

  @Test
  public void shouldNotExecuteProcessWhenLockNotAcquired() throws Exception {
    // given
    doThrow(new LockCheckException()).when(lockChecker).acquireLockDefault();
    runner.setThrowExceptionIfCannotObtainLock(false);
    // when
    runner.execute();

    // then
    verify(changeEntryRepository, never()).isNewChange(any(ChangeEntry.class));
  }

  @Test(expected = MongockException.class)
  public void shouldNotExecuteProcessAndThrowsExceptionWhenLockNotAcquiredAndFlagThrowExceptionIfLockNotAcquiredTrue()
      throws Exception {
    // given
    doThrow(new LockCheckException("")).when(lockChecker).acquireLockDefault();
    TestUtils.setField(runner, "throwExceptionIfCannotObtainLock", true);

    // when
    runner.execute();

  }

  @Test
  public void shouldReturnExecutionStatusBasedOnDao() throws Exception {
    // given
    when(lockChecker.isLockHeld()).thenReturn(true);

    boolean inProgress = runner.isExecutionInProgress();

    // then
    assertTrue(inProgress);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void shouldReleaseLockWhenExceptionInMigration() throws Exception {

    // given
    // would be nicer with a mock for the whole execution, but this would mean breaking out to separate class..
    // this should be "good enough"
    when(changeEntryRepository.isNewChange(any(ChangeEntry.class))).thenThrow(RuntimeException.class);

    // when
    // have to catch the exception to be able to verify after
    try {
      runner.execute();
    } catch (Exception e) {
      // do nothing
    }
    // then
    verify(lockChecker).releaseLockDefault();
  }

  @Test
  public void callMongoClientWhenClosing() {
    //when
    runner.close();

    //then
    verify(mongoClient).close();
  }

//  @Test
//  public void shouldCallLockRepositoryWhenSetLockCollectionName() {
//    //when
//    runner.setCollectionName("LOCK_COLLECTION_NAME");
//
//    //then
//    verify(lockRepository, new Times(1)).setCollectionName("LOCK_COLLECTION_NAME");
//  }

  @Test
  public void shouldInjectProxyToChangeEntry() throws Exception {

    ProxiesMongockTestResource changeLog = mock(ProxiesMongockTestResource.class);

    when(changeEntryRepository.isNewChange(any(ChangeEntry.class))).thenReturn(true);
    doReturn(Collections.singletonList(ProxiesMongockTestResource.class))
        .when(changeService).fetchChangeLogs();
    doReturn(changeLog).when(changeService).createInstance(any(Class.class));
    doReturn(Arrays.asList(
        ProxiesMongockTestResource.class.getDeclaredMethod("testInsertWithDB", DB.class),
        ProxiesMongockTestResource.class.getDeclaredMethod("testMongoDatabase", MongoDatabase.class)))
        .when(changeService).fetchChangeSets(any(Class.class));

    when(changeEntryRepository.isNewChange(any(ChangeEntry.class))).thenReturn(true);
    // given
    when(changeEntryRepository.isNewChange(any(ChangeEntry.class))).thenReturn(true);
    runner.setChangelogMongoDatabase(fakeMongoDatabase);

    DB proxyDb = mock(DB.class);
    runner.setChangelogDb(proxyDb);

    MongoDatabase proxyMongoDatabase = mock(MongoDatabase.class);
    runner.setChangelogMongoDatabase(proxyMongoDatabase);

    // when
    runner.execute();

    //then
    verify(changeLog, new Times(1)).testInsertWithDB(proxyDb);
    verify(changeLog, new Times(1)).testMongoDatabase(proxyMongoDatabase);
  }

  @Test
  public void shouldUseConcreteChangeLogIfFound() {
    // given
    ObjectToVerify spy = spy(new ObjectToVerify());
    MongockTestResourceWithField changeLog = new MongockTestResourceWithField(spy);
    temp.setConcreteChangeLogs(Sets.newHashSet(changeLog));
    runner = spy(temp);

    // when
    runner.execute();

    // then
    verify(spy, Mockito.times(1)).methodToVerify();

  }

  @Test
  public void shouldNotUseConcreteChangeLogIfNoChangeLogAnnotation() {
    // given
    ObjectToVerify spy = spy(new ObjectToVerify());
    MongockTestResourceWithFieldNoAnnotation changeLog = new MongockTestResourceWithFieldNoAnnotation(spy);
    temp.setConcreteChangeLogs(Sets.newHashSet(changeLog));
    runner = spy(temp);

    // when
    runner.execute();

    // then
    verify(spy, never()).methodToVerify();

  }

  private static class MongockTestResourceWithFieldNoAnnotation {

    private ObjectToVerify object;

    MongockTestResourceWithFieldNoAnnotation(ObjectToVerify object) {
      this.object = object;
    }

    @ChangeSet(author = "testuser", id = "Ctest1", order = "01", runAlways = true)
    public void testChangeSet() {

      object.methodToVerify();

    }
  }

  public static class ObjectToVerify {

    public void methodToVerify() {

    }

  }

}

//TODO move to builder test

//  @Test
//  public void shouldFailValidationWhenConcreteChangeLogIsNotInPackage() throws Exception {
//
//    // given
//    MongockBuilder builder = new MongockBuilder(mongoClient,"mongocktest", ProxiesMongockTestResource.class.getPackage().getName())
//        .setEnabled(true)
//        .setThrowExceptionIfCannotObtainLock(true)
//        .addChangeLog(new MongockTestResource());
//
//
//    // expect exception with message
//    thrown.expect(MongockException.class);
//    thrown.expectMessage(CoreMatchers.equalTo("All change logs must be part of the change log scanned package"));
//
//    // when
//    builder.build();
//
//  }

//  @Test(expected = MongockException.class)
//  public void shouldThrowAnExceptionIfNoDbNameSet() throws Exception {
//    Mongock runner = new Mongock(new MongoClientURI("mongodb://localhost:27017/"));
//    runner.setEnabled(true);
//    runner.setChangeLogsScanPackage(MongockTestResource.class.getPackage().getName());
//    runner.execute();
//  }

//  @Test
//  public void shouldCallLockCheckerWhenSetLockMaxWait() {
//    //when
//    runner.setChangeLogLockWaitTime(100);
//
//    //then
//    verify(lockChecker).setLockMaxWaitMillis(new TimeUtils().minutesToMillis(100));
//  }

//  @Test
//  public void shouldCallLockCheckerMethodsWhenSetLockConfig() {
//    //given
//    when(lockChecker.setLockAcquiredForMillis(anyLong())).thenReturn(lockChecker);
//    when(lockChecker.setLockMaxWaitMillis(anyLong())).thenReturn(lockChecker);
//    when(lockChecker.setLockMaxTries(anyInt())).thenReturn(lockChecker);
//    //when
//    runner.setLockConfig(3, 4, 5);
//
//    //then
//    verify(lockChecker, new Times(1)).setLockAcquiredForMillis(new TimeUtils().minutesToMillis(3));
//    verify(lockChecker, new Times(1)).setLockMaxWaitMillis(new TimeUtils().minutesToMillis(4));
//    verify(lockChecker, new Times(1)).setLockMaxTries(5);
////    verify(runner, new Times(1)).setThrowExceptionIfCannotObtainLock(true);
//  }

//  @Test
//  public void shouldCallLockCheckerWithDefaultConfigMethodsWhenSetLockQuickConfig() {
//    //given
//    when(lockChecker.setLockAcquiredForMillis(anyLong())).thenReturn(lockChecker);
//    when(lockChecker.setLockMaxWaitMillis(anyLong())).thenReturn(lockChecker);
//    when(lockChecker.setLockMaxTries(anyInt())).thenReturn(lockChecker);
//    //when
//    runner.setLockQuickConfig();
//
//    //then
//    verify(lockChecker, new Times(1)).setLockAcquiredForMillis(new TimeUtils().minutesToMillis(3));
//    verify(lockChecker, new Times(1)).setLockMaxWaitMillis(new TimeUtils().minutesToMillis(4));
//    verify(lockChecker, new Times(1)).setLockMaxTries(3);
//    verify(runner, new Times(1)).setThrowExceptionIfCannotObtainLock(true);
//  }
