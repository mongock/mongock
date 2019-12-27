package com.github.cloudyrock.mongock;

import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SpringMongockTest extends SpringMongockTestBase {

  @Test
  public void shouldExecuteAllChangeSets() throws Exception {
    // given
    when(changeEntryRepository.isNewChange(any(ChangeEntry.class))).thenReturn(true);

    // when
    runner.execute();

    // then
    verify(changeEntryRepository, times(11)).save(any(ChangeEntry.class)); // 13 changesets saved to dbchangelog

    // dbchangelog collection checking
    long change1 = mongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document()
        .append(ChangeEntry.KEY_CHANGE_ID, "test1")
        .append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(1, change1);
    long change2 = mongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document()
        .append(ChangeEntry.KEY_CHANGE_ID, "test2")
        .append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(1, change2);

    long change4 = mongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document()
        .append(ChangeEntry.KEY_CHANGE_ID, "test4")
        .append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(1, change4);
    long change5 = mongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document()
        .append(ChangeEntry.KEY_CHANGE_ID, "test5")
        .append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(1, change5);

    long changeAll = mongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document()
        .append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(10, changeAll);
  }

  @Test
  public void shouldPassOverChangeSets() throws Exception {
    // given
    when(changeEntryRepository.isNewChange(any(ChangeEntry.class))).thenReturn(false);

    // when
    runner.execute();

    // then
    verify(changeEntryRepository, times(1)).save(any(ChangeEntry.class)); // no changesets saved to dbchangelog
  }

  @Test
  public void shouldUsePreConfiguredMongoTemplate() throws Exception {
    MongoTemplate mt = mock(MongoTemplate.class);
    when(mt.getCollectionNames()).thenReturn(Collections.EMPTY_SET);
    when(changeEntryRepository.isNewChange(any(ChangeEntry.class))).thenReturn(true);
    runner.setMongoTemplate(mt);
    runner.afterPropertiesSet();
    verify(mt).getCollectionNames();
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
