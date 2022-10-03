package io.mongock.driver.api.entry;

import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.common.RepositoryIndexable;
import io.mongock.utils.Process;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static io.mongock.driver.api.entry.ChangeState.IGNORED;
import static io.mongock.driver.api.entry.ChangeState.FAILED;
import static io.mongock.driver.api.entry.ChangeState.ROLLBACK_FAILED;
import io.mongock.utils.field.FieldUtil;
import java.lang.reflect.Field;
import java.util.Date;


public interface ChangeEntryService extends IChangeEntryRepository<ChangeEntry, EmptyChangeEntryQuery>, RepositoryIndexable, Process {

  /**
   * NOT USED
   */
  @Deprecated
  default boolean isAlreadyExecuted(String changeSetId, String author) {
    throw new UnsupportedOperationException("THIS IS DEPRECATED AND WILL BE REMOVED");
  }

  /**
   * Retrieves a list with current executed entries ordered by execution timestamp.
   *
   * @return list of current executed entries ordered by execution timestamp
   * @throws MongockException
   */
  default List<ChangeEntryExecuted> getExecuted() throws MongockException {
    return getExecuted(null);
  }

  /**
   * Retrieves a list of the  entries in database with the current relevant state ordered by execution timestamp.
   *
   * @return list of the  entries in database with the current relevant state ordered by execution timestamp
   * @throws MongockException
   */
  default List<ChangeEntry> getAllEntriesWithCurrentState() throws MongockException {
    return getAllEntriesWithCurrentState(null);
  }

  default Map<String, List<ChangeEntry>> getEntriesMap() {
    return getEntriesMap(null);
  }

  /**
   * Returns all the changeEntries
   *
   * @return
   */
  default List<ChangeEntry> getEntriesLog(EmptyChangeEntryQuery query) {
    return getEntriesLog();
  }


  List<ChangeEntry> getEntriesLog();

  default  void deleteAll(EmptyChangeEntryQuery changeEntry) {
    deleteAll();
  }

  void deleteAll();

  default void ensureAllFields() {
    FieldUtil.getAllFields(ChangeEntry.class)
        .forEach(this::ensureField);
  }

  /**
   * Ensures that the specified entity field exists, or create it if not.
   * @param field Field to be ensured or created
   */
  void ensureField(Field field);
}



