package io.mongock.driver.mongodb.test.template.interfaces;


import io.mongock.api.exception.MongockException;
import io.mongock.driver.core.lock.LockPersistenceException;
import org.junit.jupiter.api.Test;


public interface MongoLockRepositoryITestInterface {


  @Test
  void ensureKeyUniqueness();

  @Test
  void findByKeyShouldReturnLockWhenThereIsOne() throws LockPersistenceException, MongockException;


  @Test
  void insertUpdateShouldInsertWhenEmpty() throws LockPersistenceException, MongockException;

  @Test
  void insertUpdateShouldUpdateWhenExpiresAtIsGraterThanSaved() throws LockPersistenceException, MongockException;

  @Test
  void insertUpdateShouldUpdateWhenSameOwner() throws LockPersistenceException, MongockException;

  @Test
  void insertUpdateShouldThrowExceptionWhenLockIsInDBWIthDifferentOwnerAndNotExpired() throws LockPersistenceException, MongockException;

  @Test
  void removeShouldRemoveWhenSameOwner() throws LockPersistenceException, MongockException;

  @Test
  void removeShouldNotRemoveWhenDifferentOwner() throws LockPersistenceException, MongockException;

  @Test
  void updateIfSameOwnerShouldNotInsertWhenEmpty() throws LockPersistenceException, MongockException;

  @Test
  void updateIfSameOwnerShouldNotUpdateWhenExpiresAtIsGraterThanSavedButOtherOwner() throws LockPersistenceException, MongockException;

  @Test
  void updateIfSameOwnerShouldUpdateWhenSameOwner() throws LockPersistenceException, MongockException;

  @Test
  void updateIfSameOwnerShouldNotUpdateWhenDifferentOwnerAndExpiresAtIsNotGrater() throws LockPersistenceException, MongockException;


  void initializeRepository() ;

}
