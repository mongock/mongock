package com.github.cloudyrock.mongock.driver.mongodb.sync.v4.driver;

import com.github.cloudyrock.mongock.MongockConnectionDriver;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.decorator.impl.MongoDataBaseDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.repository.MongoLockRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.changock.driver.api.driver.ChangeSetDependency;
import io.changock.driver.api.entry.ChangeEntry;
import io.changock.driver.api.lock.LockManager;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvokerImpl;
import io.changock.driver.core.driver.ConnectionDriverBase;
import io.changock.driver.core.lock.LockRepository;
import io.changock.migration.api.exception.ChangockException;
import io.changock.utils.annotation.NotThreadSafe;
import org.bson.Document;

import java.util.HashSet;
import java.util.Set;

@NotThreadSafe
public abstract class MongoSync4DriverBase<CHANGE_ENTRY extends ChangeEntry>
    extends ConnectionDriverBase<CHANGE_ENTRY>
    implements MongockConnectionDriver<CHANGE_ENTRY> {

  private static final String DEFAULT_CHANGELOG_COLLECTION_NAME = "mongockChangeLog";
  private static final String DEFAULT_LOCK_COLLECTION_NAME = "mongockLock";

  protected final MongoDatabase mongoDatabase;
  protected String changeLogCollectionName = DEFAULT_CHANGELOG_COLLECTION_NAME;
  protected String lockCollectionName = DEFAULT_LOCK_COLLECTION_NAME;
  protected MongoLockRepository lockRepository;

  public MongoSync4DriverBase(MongoDatabase mongoDatabase) {
    this.mongoDatabase = mongoDatabase;
  }

  public void setChangeLogCollectionName(String changeLogCollectionName) {
    this.changeLogCollectionName = changeLogCollectionName;
  }

  public void setLockCollectionName(String lockCollectionName) {
    this.lockCollectionName = lockCollectionName;
  }

  @Override
  public void runValidation() throws ChangockException {
    if (mongoDatabase == null) {
      throw new ChangockException("MongoDatabase cannot be null");
    }
    if (this.getLockManager() == null) {
      throw new ChangockException("Internal error: Driver needs to be initialized by the runner");
    }
  }

  @Override
  protected LockRepository getLockRepository() {
    if (lockRepository == null) {
      MongoCollection<Document> collection = mongoDatabase.getCollection(lockCollectionName);
      this.lockRepository = new MongoLockRepository(collection);
    }
    return lockRepository;
  }

  @Override
  public Set<ChangeSetDependency> getDependencies() {
    LockManager lockManager = this.getLockManager();
    Set<ChangeSetDependency> dependencies = new HashSet<>();
    MongoDataBaseDecoratorImpl mongoDataBaseDecorator = new MongoDataBaseDecoratorImpl(mongoDatabase, new LockGuardInvokerImpl(lockManager));
    dependencies.add(new ChangeSetDependency(MongoDatabase.class, mongoDataBaseDecorator));
    return dependencies;
  }
}
