package io.mongock.driver.api.common;

import io.mongock.utils.Process;

//TODO move this to an association class

public interface EntityRepository<DOMAIN_CLASS, ENTITY_CLASS> extends Process {

  /**
   * Transform a domain object to its persistence representation
   *
   * @param domain domain object that requires to be persisted
   * @return persistence representation of the domain object
   */
  ENTITY_CLASS toEntity(DOMAIN_CLASS domain);

}
