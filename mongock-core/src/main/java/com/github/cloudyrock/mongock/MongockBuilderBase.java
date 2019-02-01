package com.github.cloudyrock.mongock;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.github.cloudyrock.mongock.StringUtils.hasText;

public abstract class MongockBuilderBase<RETURN_TYPE extends MongockBuilderBase> {

  //Mandatory
  final MongoClient mongoClient;
  String changeLogsScanPackage;
  String databaseName;

  //Optionals
  long lockAcquiredForMinutes = 24L * 60L;
  long maxWaitingForLockMinutes = 3L;
  int maxTries = 1;
  boolean throwExceptionIfCannotObtainLock = false;
  boolean enabled = true;
  String changeLogCollectionName = "mongockChangeLog";
  String lockCollectionName = "mongockLock";


  /**
   * <p>Builder constructor takes db.mongodb.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about <tt>MongoClient</tt> please see com.mongodb.MongoClient docs
   * </p>
   * @param mongoClient database connection client
   * @param databaseName database name
   * @param changeLogsScanPackage package path where the changelogs are located
   * @see MongoClient
   */
  public MongockBuilderBase(MongoClient mongoClient, String databaseName, String changeLogsScanPackage) {
    this.mongoClient = mongoClient;
    this.databaseName = databaseName;
    this.changeLogsScanPackage = changeLogsScanPackage;
  }

  protected abstract RETURN_TYPE returnInstance();

  /**
   * <p>Changes the changelog collection name</p>
   * <p>Be careful as changing the changelog collection name can make Mongock to undesirably run twice the same changelog</p>
   * @param changeLogCollectionName name of the collection
   * @return Mongock builder
   */
  public RETURN_TYPE setChangeLogCollectionName(String changeLogCollectionName) {
    this.changeLogCollectionName = changeLogCollectionName;
    return returnInstance();
  }

  /**
   * <p>Changes the lock collection name</p>
   * <p>Be careful as changing the lock collection name can make Mongock to run twice the same changelog and other
   * undesirable scenarios</p>
   * @param lockCollectionName name of the collection
   * @return Mongock builder
   */
  public RETURN_TYPE setLockCollectionName(String lockCollectionName) {
    this.lockCollectionName = lockCollectionName;
    return returnInstance();
  }

  /**
   * Feature which enables/disables throwing MongockException if the lock cannot be obtained
   *
   * @param throwExceptionIfCannotObtainLock Mongock will throw MongockException if lock can not be obtained
   * @return Mongock builder
   */
  public RETURN_TYPE setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
    this.throwExceptionIfCannotObtainLock = throwExceptionIfCannotObtainLock;
    return returnInstance();
  }


  /**
   * Feature which enables/disables execution
   *
   * @param enabled Migration process will run only if this option is set to true
   * @return Mongock builder
   */
  public RETURN_TYPE setEnabled(boolean enabled) {
    this.enabled = enabled;
    return returnInstance();
  }

  /**
   * Set up the lock with minimal configuration. This implies Mongock will throw an exception if cannot obtains the lock.
   *
   * @param lockAcquiredForMinutes   Acquired time in minutes
   * @param maxWaitingForLockMinutes max time in minutes to wait for the lock in each try.
   * @param maxTries                 number of tries
   * @return Mongock object for fluent interface
   */
  public RETURN_TYPE setLockConfig(long lockAcquiredForMinutes, long maxWaitingForLockMinutes, int maxTries) {
    this.lockAcquiredForMinutes = lockAcquiredForMinutes;
    this.maxWaitingForLockMinutes = maxWaitingForLockMinutes;
    this.maxTries = maxTries;
    this.throwExceptionIfCannotObtainLock = true;
    return returnInstance();
  }

  /**
   * Set up the lock with default configuration to wait for it and through an exception when cannot obtain it.
   *
   * @return Mongock object for fluent interface
   */
  public RETURN_TYPE setLockQuickConfig() {
    setLockConfig(3, 4, 3);
    return returnInstance();
  }


  void validateMandatoryFields() throws MongockException {
    if (mongoClient == null) {
      throw new MongockException("MongoClient cannot be null");
    }
    if (!hasText(databaseName)) {
      throw new MongockException("DB name is not set. It should be defined in MongoDB URI or via setter");
    }
    if (!hasText(changeLogsScanPackage)) {
      throw new MongockException("Scan package for changelogs is not set: use appropriate setter");
    }
  }
}
