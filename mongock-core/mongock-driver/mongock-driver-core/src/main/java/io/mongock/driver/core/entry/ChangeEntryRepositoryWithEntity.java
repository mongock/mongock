package io.mongock.driver.core.entry;

import io.mongock.driver.api.common.EntityRepository;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.driver.api.entry.ChangeEntryService;

public interface ChangeEntryRepositoryWithEntity<ENTITY_CLASS> extends ChangeEntryService, EntityRepository<ChangeEntry, ENTITY_CLASS> {

}
