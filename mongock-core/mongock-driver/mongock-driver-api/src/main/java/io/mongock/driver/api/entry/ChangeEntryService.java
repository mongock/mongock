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


public interface ChangeEntryService extends RepositoryIndexable, Process {

  /**
   * Retrieves if a changeSet with given changeSetId and author hasn't been already executed. This means
   * there is no changeSet in the changeLog store for the given changeSetId and author, or its state is not
   * EXECUTED.
   *
   * @param changeSetId changeSet id
   * @param author      changeSet's author
   * @return true if it has not been executed yet, false otherwise
   * @throws MongockException if anything goes wrong
   */
  boolean isAlreadyExecuted(String changeSetId, String author) throws MongockException;

  /**
   * Retrieves a list with current executed entries ordered by execution timestamp.
   *
   * @return list of current executed entries ordered by execution timestamp
   * @throws MongockException
   */
  default List<ExecutedChangeEntry> getExecuted() throws MongockException {

    Predicate<ChangeEntry> cleanIrrelevantState = entry -> entry.getState() != IGNORED && entry.getState() != FAILED && entry.getState() != ROLLBACK_FAILED;
    return getEntriesMap()//Maps of List<ChangeEntry>, indexed by changeId
        .values()//collection of List<ChangeEntry>
        .stream()
        .map(duplicatedEntries -> duplicatedEntries.stream().filter(cleanIrrelevantState).collect(Collectors.toList()))//only takes into account executed or rolled back
        .filter(duplicatedEntries -> !duplicatedEntries.isEmpty())
        .map(duplicatedEntries -> duplicatedEntries.get(0))//transform each list in a single ChangeEntry(the first one)
        .sorted(Comparator.comparing(ChangeEntry::getTimestamp))// Sorts the resulting list chronologically
        .filter(ChangeEntry::isExecuted)//only gets the ones that are executed
        .map(ExecutedChangeEntry::new)//transform the entry to an executed entry
        .collect(Collectors.toList());
  }

  /**
   * Retrieves a list of the  entries in database with the current relevant state ordered by execution timestamp.
   *
   * @return list of the  entries in database with the current relevant state ordered by execution timestamp
   * @throws MongockException
   */
  default List<ChangeEntry> getAllEntriesWithCurrentState() throws MongockException {
    return getEntriesMap()//Maps of List<ChangeEntry>, indexed by changeId
        .values()//collection of List<ChangeEntry>
        .stream()
        .map(duplicatedEntries -> duplicatedEntries.stream().filter(ChangeEntry::hasRelevantState).collect(Collectors.toList()))//only takes into account relevant states
        .filter(duplicatedEntries -> !duplicatedEntries.isEmpty())
        .map(duplicatedEntries -> duplicatedEntries.get(0))//transform each list in a single ChangeEntry(the first one)
        .sorted(Comparator.comparing(ChangeEntry::getTimestamp))// Sorts the resulting list chronologically
        .collect(Collectors.toList());
  }

  default Map<String, List<ChangeEntry>> getEntriesMap() {
    Map<String, List<ChangeEntry>> log = getEntriesLog()
        .stream()
        .collect(Collectors.groupingBy(ChangeEntry::getChangeId));
    log.values().forEach(entries -> entries.sort((c1, c2) -> c2.getTimestamp().compareTo(c1.getTimestamp())));//sorts each list in the map by date in reverse
    return log;
  }

  /**
   * Returns all the changeEntries
   *
   * @return
   */
  List<ChangeEntry> getEntriesLog();


  /**
   * If there is already an ChangeEntry the same executionId, id and author, it will be updated. Otherwise,
   * this method will be inserted.
   *
   * @param changeEntry Entry to be inserted
   * @throws MongockException if any i/o exception or already inserted
   */
  void upsert(ChangeEntry changeEntry) throws MongockException;

}
