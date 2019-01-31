package com.github.cloudyrock.mongock;

import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JongoMongock extends Mongock implements IMongock {
  private static final Logger logger = LoggerFactory.getLogger(JongoMongock.class);
  private Jongo jongo;

  protected JongoMongock(ChangeEntryRepository changeEntryRepository, MongoClient mongoClient, ChangeService changeService, LockChecker lockChecker) {
    super(changeEntryRepository, mongoClient, changeService, lockChecker);
  }

  @Override
  protected void executeChangeSetMethod(Method changeSetMethod, Object changeLogInstance)
      throws IllegalAccessException, InvocationTargetException {
    if (changeSetMethod.getParameterTypes().length == 1
        && changeSetMethod.getParameterTypes()[0].equals(Jongo.class)) {
      logger.debug("method with Jongo argument");
      changeSetMethod.invoke(changeLogInstance, this.jongo);
    } else {
      super.executeChangeSetMethod(changeSetMethod, changeLogInstance);
    }
  }


  /**
   * Sets pre-configured {@link Jongo} instance to use by the Mongock
   *
   * @param jongo {@link Jongo} instance
   * @return JongoMongock object for fluent interface
   */
  JongoMongock setJongo(Jongo jongo) {
    this.jongo = jongo;
    return this;
  }
}
