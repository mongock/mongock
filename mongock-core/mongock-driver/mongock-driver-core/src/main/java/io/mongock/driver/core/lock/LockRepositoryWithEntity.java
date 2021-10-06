package io.mongock.driver.core.lock;

import io.mongock.driver.api.common.EntityRepository;

public interface LockRepositoryWithEntity<ENTITY_CLASS> extends LockRepository, EntityRepository<LockEntry, ENTITY_CLASS> {
}
