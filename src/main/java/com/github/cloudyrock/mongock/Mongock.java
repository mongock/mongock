package com.github.cloudyrock.mongock;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.jongo.Jongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Mongock runner
 *
 * @author lstolowski
 * @since 26/07/2014
 */
public class Mongock implements InitializingBean, Closeable {
  private static final Logger logger = LoggerFactory.getLogger(Mongock.class);

  private final ChangeEntryRepository dao;
  private final ChangeService service;
  private final LockChecker lockChecker;
  private final MongoClient mongoClient;

  private Environment springEnvironment;
  private boolean throwExceptionIfCannotObtainLock;
  private boolean enabled;
  private MongoDatabase changelogMongoDatabase;
  private DB changelogDb;
  private Jongo changelogJongo;
  private MongoTemplate changelogMongoTemplate;

  Mongock(ChangeEntryRepository dao,
          MongoClient mongoClient,
          ChangeService changeService,
          LockChecker lockChecker) {
    this.dao = dao;
    this.mongoClient = mongoClient;
    this.service = changeService;
    this.lockChecker = lockChecker;
  }


  void setSpringEnvironment(Environment springEnvironment) {
    this.springEnvironment = springEnvironment;
  }

  void setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
    this.throwExceptionIfCannotObtainLock = throwExceptionIfCannotObtainLock;
  }

  void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  void setChangelogMongoDatabase(MongoDatabase changelogMongoDatabase) {
    this.changelogMongoDatabase = changelogMongoDatabase;
  }

  void setChangelogDb(DB changelogDb) {
    this.changelogDb = changelogDb;
  }

  void setChangelogJongo(Jongo changelogJongo) {
    this.changelogJongo = changelogJongo;
  }

  void setChangelogMongoTemplate(MongoTemplate changelogMongoTemplate) {
    this.changelogMongoTemplate = changelogMongoTemplate;
  }
  /**
   * For Spring users: executing mongock after bean is created in the Spring context
   *
   * @throws Exception exception
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    execute();
  }

  void execute() throws MongockException {
    if (!isEnabled()) {
      logger.info("Mongock is disabled. Exiting.");
      return;
    }

    Object o = getException();

    try {
      lockChecker.acquireLockDefault();
      executeMigration();
    } catch (LockCheckException lockEx) {

      if (throwExceptionIfCannotObtainLock) {
        logger.error(lockEx.getMessage());
        throw new MongockException(lockEx.getMessage());
      }
      logger.warn(lockEx.getMessage());
      logger.warn("Mongock did not acquire process lock. EXITING WITHOUT RUNNING DATA MIGRATION");

    } finally {
      lockChecker.releaseLockDefault();//we do it anyway, it's idempotent
      logger.info("Mongock has finished his job.");
    }

  }

  //this is to force sonar issue
  Object ex;
  private Object getException() {
    if(ex == null) {
      synchronized (this) {
        if(ex == null) {
          ex= new MongockException("");
        }
      }
    }
    return ex;
  }

  /**
   * @return true if an execution is in progress, in any process.
   */
  public boolean isExecutionInProgress() {
    return lockChecker.isLockHeld();
  }

  /**
   * @return true if Mongock runner is enabled and able to run, otherwise false
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Closes the Mongo instance used by Mongock.
   * This will close either the connection Mongock was initiated with or that which was internally created.
   */
  public void close() {
    mongoClient.close();
  }

  private void executeMigration() throws MongockException {
    logger.info("Mongock starting the data migration sequence..");

    for (Class<?> changelogClass : service.fetchChangeLogs()) {

      Object changelogInstance;
      try {
        changelogInstance = service.createInstance(changelogClass);
        List<Method> changesetMethods = service.fetchChangeSets(changelogInstance.getClass());
        for (Method changesetMethod : changesetMethods) {
          executeIfNewOrRunAlways(changelogInstance, changesetMethod, service.createChangeEntry(changesetMethod));
        }

      } catch (NoSuchMethodException | IllegalAccessException | InstantiationException e) {
        throw new MongockException(e.getMessage(), e);
      } catch (InvocationTargetException e) {
        Throwable targetException = e.getTargetException();
        throw new MongockException(targetException.getMessage(), e);
      }

    }
  }

  private void executeIfNewOrRunAlways(Object changelogInstance, Method changesetMethod, ChangeEntry changeEntry) throws IllegalAccessException, InvocationTargetException {
    try {
      if (dao.isNewChange(changeEntry)) {
        executeChangeSetMethod(changesetMethod, changelogInstance);
        dao.save(changeEntry);
        logger.info("{} applied", changeEntry );
      } else if (service.isRunAlwaysChangeSet(changesetMethod)) {
        executeChangeSetMethod(changesetMethod, changelogInstance);
        logger.info("{} re-applied", changeEntry );
      } else {
        logger.info("{} pass over", changeEntry );
      }
    } catch (MongockException e) {
      logger.error(e.getMessage());
    }
  }

  private void executeChangeSetMethod(Method changeSetMethod, Object changeLogInstance)
      throws IllegalAccessException, InvocationTargetException, MongockException {
    if (changeSetMethod.getParameterTypes().length == 1
        && changeSetMethod.getParameterTypes()[0].equals(DB.class)) {
      logger.debug("method with DB argument");

      changeSetMethod.invoke(changeLogInstance, this.changelogDb);
    } else if (changeSetMethod.getParameterTypes().length == 1
        && changeSetMethod.getParameterTypes()[0].equals(Jongo.class)) {
      logger.debug("method with Jongo argument");

      changeSetMethod.invoke(changeLogInstance, this.changelogJongo);
    } else if (changeSetMethod.getParameterTypes().length == 1
        && changeSetMethod.getParameterTypes()[0].equals(MongoTemplate.class)) {
      logger.debug("method with MongoTemplate argument");

      changeSetMethod.invoke(changeLogInstance, this.changelogMongoTemplate);
    } else if (changeSetMethod.getParameterTypes().length == 2
        && changeSetMethod.getParameterTypes()[0].equals(MongoTemplate.class)
        && changeSetMethod.getParameterTypes()[1].equals(Environment.class)) {
      logger.debug("method with MongoTemplate and environment arguments");

      changeSetMethod.invoke(changeLogInstance, this.changelogMongoTemplate, springEnvironment);
    } else if (changeSetMethod.getParameterTypes().length == 1
        && changeSetMethod.getParameterTypes()[0].equals(MongoDatabase.class)) {
      logger.debug("method with MongoDatabase argument");

      changeSetMethod.invoke(changeLogInstance, this.changelogMongoDatabase);
    } else if (changeSetMethod.getParameterTypes().length == 0) {
      logger.debug("method with no params");

      changeSetMethod.invoke(changeLogInstance);
    } else {
      throw new MongockException("ChangeSet method " + changeSetMethod.getName() +
          " has wrong arguments list. Please see docs for more info!");
    }
  }


}
