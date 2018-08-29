package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.test.proxy.ProxiesMongockTestResource;
import com.github.fakemongo.Fongo;
import com.mongodb.DB;
import com.mongodb.FongoDB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.verification.Times;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.singletonList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @since 04/04/2018
 */
public class MongockLockCheckerIntegrationTest {

  private static final String LOCK_COLLECTION_NAME = "mongocklock";
  private static final Set<String> unInterceptedMethods =
      new HashSet<>(Arrays.asList("getCollection", "getCollectionFromString", "getDatabase", "toString"));
  private static final Set<String> proxyCreatordMethods =
      new HashSet<>(Arrays.asList("getCollection", "getCollectionFromString", "getDatabase"));
  private Mongock runner;
  private MongoDatabase mongoDatabase;
  private ChangeService changeService;
  private LockRepository lockRepository;
  private TimeUtils timeUtils;
  private LockChecker lockChecker;
  private ProxyFactory proxyFactory;
  private ChangeEntryRepository dao;
  private MongockBuilder builder;
  private FongoDB db;

  @Before
  public void setUp() throws NoSuchMethodException, MongockException {
    db = new Fongo("testServer").getDB("mongocktest");
    mongoDatabase = spy(new Fongo("testServer").getDatabase("mongocktest"));

    MongoClient mongoClient = mock(MongoClient.class);
    when(mongoClient.getDatabase(anyString())).thenReturn(mongoDatabase);
    when(mongoClient.getDB(anyString())).thenReturn(db);

    builder = new MongockBuilder(mongoClient,"mongocktest", ProxiesMongockTestResource.class.getPackage().getName())
        .setEnabled(true)
        .setThrowExceptionIfCannotObtainLock(true);

    timeUtils = spy(new TimeUtils());
    changeService = spy(new ChangeService());
    lockRepository = spy(new LockRepository(LOCK_COLLECTION_NAME, mongoDatabase));

    lockChecker = spy(new LockChecker(lockRepository, timeUtils));

    PreInterceptor preInterceptor = new PreInterceptor() {
      @Override
      public void before() {
        try {
          lockChecker.ensureLockDefault();
        } catch (LockCheckException e) {
          throw new RuntimeException(e);
        }
      }
    };
    proxyFactory = new ProxyFactory(preInterceptor, proxyCreatordMethods, unInterceptedMethods);
    doReturn(singletonList(ProxiesMongockTestResource.class))
        .when(changeService).fetchChangeLogs();
    doReturn(singletonList(ProxiesMongockTestResource.class.getDeclaredMethod("testInsertWithDB", DB.class)))
        .when(changeService).fetchChangeSets(ProxiesMongockTestResource.class);

    this.dao = mock(ChangeEntryRepository.class);
  }

  @Test
  public void shouldCallEnsureLock() throws Exception {
    when(dao.isNewChange(any(ChangeEntry.class))).thenReturn(true);
    MongoDatabase mongoDatabaseProxy = proxyFactory.createProxyFromOriginal(mongoDatabase);
    DB dbProxy = proxyFactory.createProxyFromOriginal(db);
    runner = builder.build(dao, changeService, lockChecker, mongoDatabaseProxy, dbProxy);
    // when
    runner.execute();

    //then
    verify(lockChecker, new Times(1)).acquireLockDefault();
    verify(lockChecker, new Times(2)).ensureLockDefault();
  }

  @Test
  public void ensureLockShouldTryToRefreshLockIfExpiredButStillOwner() throws Exception {
    //given
    int tenMinutes = 10 * 60 * 1000;
    doReturn(new Date(System.currentTimeMillis() - tenMinutes))
        .doReturn(new Date(System.currentTimeMillis() + tenMinutes))
        .when(timeUtils).currentTime();
    when(dao.isNewChange(any(ChangeEntry.class))).thenReturn(true);
    MongoDatabase mongoDatabaseProxy = proxyFactory.createProxyFromOriginal(mongoDatabase);
    DB dbProxy = proxyFactory.createProxyFromOriginal(db);
    runner = builder.build(dao, changeService, lockChecker, mongoDatabaseProxy, dbProxy);

    // when
    runner.execute();

    //then
    verify(lockChecker, new Times(1)).acquireLockDefault();
    verify(lockChecker, new Times(2)).ensureLockDefault();
    verify(lockRepository, new Times(1)).insertUpdate(any(LockEntry.class));
    verify(lockRepository, new Times(1)).updateIfSameOwner(any(LockEntry.class));

  }

  @Test(expected = MongockException.class)
  public void ensureLockShouldThrowExceptionWhenLockIsStolenByAnotherProcess() throws Exception {
    //given
    doReturn(new Date(System.currentTimeMillis() + 5000))
        .when(timeUtils).currentTimePlusMillis(anyLong());

    PreInterceptor preInterceptor = new PreInterceptor() {
      @Override
      public void before() {
        try {
          lockChecker.ensureLockDefault();
          replaceCurrentLockWithOtherOwner("anotherOwner");
        } catch (LockCheckException e) {
          throw new RuntimeException(e);
        }
      }
    };
    proxyFactory = new ProxyFactory(preInterceptor, proxyCreatordMethods, unInterceptedMethods);
    when(dao.isNewChange(any(ChangeEntry.class))).thenReturn(true);
    MongoDatabase mongoDatabaseProxy = proxyFactory.createProxyFromOriginal(mongoDatabase);
    DB dbProxy = proxyFactory.createProxyFromOriginal(db);
    runner = builder.build(dao, changeService, lockChecker, mongoDatabaseProxy, dbProxy);

    // when
    runner.execute();

    //then should throw MongockException
  }

  //Private helper method to replace the lock with a different owner
  private void replaceCurrentLockWithOtherOwner(String owner) {
    Document document = new Document();
    document.append("key", "DEFAULT_LOCK")
        .append("status", "LOCK_HELD")
        .append("owner", owner)
        .append("expiresAt", System.currentTimeMillis() + 10000);
    mongoDatabase.getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document("key", "DEFAULT_LOCK"),
        new Document().append("$set", document),
        new UpdateOptions().upsert(false));
  }
//
//  //Private helper method to inject dependencies with reflection to avoid modifying production code for testing purpose
//  private void injectDependencies() throws NoSuchFieldException, IllegalAccessException {
//
//    Field changeServiceField = null;
//    Field proxyFactoryField = null;
//    Field lockCheckerField = null;
//
//    try {
//      changeServiceField = runner.getClass().getDeclaredField("service");
//      changeServiceField.setAccessible(true);
//      changeServiceField.set(runner, changeService);
//
//      proxyFactoryField = runner.getClass().getDeclaredField("proxyFactory");
//      proxyFactoryField.setAccessible(true);
//      proxyFactoryField.set(runner, proxyFactory);
//
//      lockCheckerField = runner.getClass().getDeclaredField("lockChecker");
//      lockCheckerField.setAccessible(true);
//      lockCheckerField.set(runner, lockChecker);
//
//    } finally {
//      if (changeServiceField != null) changeServiceField.setAccessible(false);
//      if (proxyFactoryField != null) proxyFactoryField.setAccessible(false);
//      if (lockCheckerField != null) lockCheckerField.setAccessible(false);
//    }
//
//  }

}
