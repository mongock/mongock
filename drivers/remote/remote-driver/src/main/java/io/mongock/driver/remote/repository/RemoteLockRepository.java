package io.mongock.driver.remote.repository;

import io.mongock.driver.core.lock.LockRepository;
import io.mongock.driver.core.lock.LockEntry;
import io.mongock.driver.core.lock.LockPersistenceException;
import io.mongock.driver.core.lock.LockStatus;
import io.mongock.driver.remote.repository.external.LockReqDto;
import io.mongock.driver.remote.repository.external.LockServiceClient;

public class RemoteLockRepository implements LockRepository {
  private final LockServiceClient client;
  private final String organization;
  private final String service;

  public RemoteLockRepository(LockServiceClient client, String organization, String service) {
    this.client = client;
    this.organization = organization;
    this.service = service;
  }

  @Override
  public void initialize() {

  }

  @Override
  public void insertUpdate(LockEntry newLock) throws LockPersistenceException {
    client.acquireLock(organization, service, buildLock(newLock, false));
  }

  @Override
  public void updateIfSameOwner(LockEntry newLock) throws LockPersistenceException {
    client.acquireLock(organization, service, buildLock(newLock, true));
  }

  @Override
  public LockEntry findByKey(String lockKey) {
    return client.getByOrganizationServiceAndKey(organization, service, lockKey);
  }

  @Override
  public void removeByKeyAndOwner(String lockKey, String owner) {
    //TODO owner is taken from headers
    client.removeByOrganizationAndServiceAndKey(organization, service, lockKey);
  }

  @Override
  public void deleteAll() {
    client.removeByOrganizationAndService(organization, service);
  }

  @Override
  public void setIndexCreation(boolean indexCreation) {
    //do nothing
  }

  private LockReqDto buildLock(LockEntry newLock, boolean onlyIfSameOwner) {
    return new LockReqDto(newLock.getKey(), LockStatus.valueOf(newLock.getStatus()), newLock.getAcquiredForMillisFromNow(), onlyIfSameOwner);
  }
}
