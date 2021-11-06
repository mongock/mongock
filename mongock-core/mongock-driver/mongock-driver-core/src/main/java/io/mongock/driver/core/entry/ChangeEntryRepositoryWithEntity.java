package io.mongock.driver.core.entry;

import io.mongock.driver.api.common.EntityRepository;
import io.mongock.driver.api.entry.ChangeEntry;

public interface ChangeEntryRepositoryWithEntity<ENTITY_CLASS> extends ChangeEntryRepository, EntityRepository<ENTITY_CLASS> {

}
