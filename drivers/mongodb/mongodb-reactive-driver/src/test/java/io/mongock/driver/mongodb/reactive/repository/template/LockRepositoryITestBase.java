package io.mongock.driver.mongodb.reactive.repository.template;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public interface LockRepositoryITestBase {

  @Test
  @DisplayName("SHOULD create unique index WHEN ensureIndex=true IF index not created")
  void shouldCreateUniqueIndex_whenEnsureIndex_IfNotCreatedYet();

  @Test
  @DisplayName("SHOULD NO create unique index WHEN ensureIndex=true IF index created")
  void shouldNoCreateUniqueIndex_whenEnsureIndex_IfAlreadyCreated() ;

  @Test
  @DisplayName("Key should be unique")
  void ensureKeyUniqueness();

  @Test
  @DisplayName("SHOULD return lock WHEN findByKey IF it's present in DB")
  void shouldReturnLockWhenFindByKeyIfItIsInDB();

  @Test
  @DisplayName("SHOULD insert LOCK WHEN insertUpdate IF collection/table is empty")
  void insertUpdateShouldInsertWhenEmpty();

  @Test
  @DisplayName("SHOULD update WHEN insertUpdate IF new lock and lock in DB have same key AND owner")
  void insertUpdateShouldUpdateWhenSameOwner();

  @Test
  @DisplayName("SHOULD replace WHEN insertUpdate IF new lock and lock ind DB have same key AND different owner AND lock in DB is expired")
  void insertUpdateShouldUpdateWhenLockIndDbIsExpired();

  @Test
  @DisplayName("SHOULD throw exception WHEN insertUpdate IF new lock and lock ind DB have same key AND different owner AND lock in DB is NOT expired ")
  void insertUpdateShouldThrowExceptionWhenLockIsInDBWIthDifferentOwnerAndNotExpired();

  @Test
  @DisplayName("SHOULD delete lock WHEN remove IF lock in DB belongs to the given owner")
  void removeShouldRemoveWhenSameOwner();

  @Test
  @DisplayName("SHOULD NOT delete lock WHEN remove IF lock does NOT belong to the given owner")
  void removeShouldNotRemoveWhenDifferentOwner() ;

  @Test
  @DisplayName("SHOULD NOT insert WHEN updateIfSameOwner IF there is no lock in DB for the given key and owner")
  void updateIfSameOwnerShouldNotInsertWhenEmpty();

  @Test
  @DisplayName("SHOULD NOT update WHEN updateIfSameOwner IF current lock in DB is expired BUT new lock and lock in DB don't share owner")
  void updateIfSameOwnerShouldNotUpdateWhenExpiresAtIsGraterThanSavedButOtherOwner();

  @Test
  @DisplayName("SHOULD update WHEN updateIfSameOwner IF new lock and lock in DB  share owner")
  void updateIfSameOwnerShouldUpdateWhenSameOwner();

  @Test
  @DisplayName("SHOULD NOT update WHEN updateIfSameOwner IF new lock and lock in DB don't have same owner and lock in DB is not expired")
  void updateIfSameOwnerShouldNotUpdateWhenDifferentOwnerAndExpiresAtIsNotGrater();
  
}
