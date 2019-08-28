package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.test.proxy.ProxiesMongockTestResource;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.verification.Times;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MongockTest extends MongockTestBase {

  @Test
  public void shouldExecuteAllChangeSets() throws Exception {
    // given
    when(changeEntryRepository.isNewChange(any(ChangeEntry.class))).thenReturn(true);

    // when
    runner.execute();

    // then
    verify(changeEntryRepository, times(19)).save(any(ChangeEntry.class)); // 21 changesets saved to dbchangelog

    // dbchangelog collection checking
    long change1 = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document()
        .append(ChangeEntry.KEY_CHANGEID, "test1")
        .append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(1, change1);
    long change2 = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document()
        .append(ChangeEntry.KEY_CHANGEID, "test2")
        .append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(1, change2);


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
    assertEquals(19, changeAll);
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
    doReturn(Collections.singletonList(ProxiesMongockTestResource.class.getDeclaredMethod("testMongoDatabase", MongoDatabase.class)))
        .when(changeService).fetchChangeSets(any(Class.class));

    when(changeEntryRepository.isNewChange(any(ChangeEntry.class))).thenReturn(true);
    // given
    when(changeEntryRepository.isNewChange(any(ChangeEntry.class))).thenReturn(true);
    runner.setChangelogMongoDatabase(fakeMongoDatabase);


    MongoDatabase proxyMongoDatabase = mock(MongoDatabase.class);
    runner.setChangelogMongoDatabase(proxyMongoDatabase);

    // when
    runner.execute();

    //then
    verify(changeLog, new Times(1)).testMongoDatabase(proxyMongoDatabase);
  }

}

//TODO move to builder test
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
