package io.mongock.driver.core.entry;

import io.mongock.driver.api.common.EntityRepository;
import io.mongock.driver.api.entry.ChangeEntry;

public interface ChangeEntryRepositoryWithEntity<CHANGE_ENTRY extends ChangeEntry, ENTITY_CLASS> extends ChangeEntryRepository<CHANGE_ENTRY>, EntityRepository<CHANGE_ENTRY, ENTITY_CLASS> {

}
