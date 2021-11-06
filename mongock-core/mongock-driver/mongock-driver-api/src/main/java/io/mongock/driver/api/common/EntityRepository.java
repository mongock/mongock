package io.mongock.driver.api.common;

import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.utils.Process;
import io.mongock.utils.field.FieldInstance;
import io.mongock.utils.field.FieldUtil;

import java.util.List;
import java.util.stream.Collectors;

public interface EntityRepository<ENTITY_CLASS> extends Process {

  /**
   * Transform a domain object to its persistence representation
   *
   * @param entry domain object that requires to be persisted
   * @return persistence representation of the domain object
   */
  default ENTITY_CLASS toEntity(ChangeEntry entry) {
    return mapFieldInstances(
        FieldUtil.getAllFields(entry.getClass())
            .stream()
            .map(field -> new FieldInstance(field, entry))
            .collect(Collectors.toList())
    );
  }

  ENTITY_CLASS mapFieldInstances(List<FieldInstance> fieldInstanceList);

}
