package io.mongock.driver.api.entry;

import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.common.RepositoryIndexable;
import io.mongock.utils.Process;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.mongock.driver.api.entry.ChangeState.EXECUTED;


public interface ChangeEntryService<CHANGE_ENTRY extends ChangeEntry> extends RepositoryIndexable, Process {

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
   * @return list of current executed entries ordered by execution timestamp
   * @throws MongockException 
   */
  default List<ExecutedChangeEntry> getExecuted() throws MongockException {
    return getAll()
        .stream()
        .collect(Collectors.groupingBy(ChangeEntry::getChangeId))//Maps of List<ChangeEntry>, indexed by changeId
        .values()//collection of List<ChangeEntry>
        .stream()
        .peek(duplicatedEntries -> duplicatedEntries.sort((c1, c2) -> c2.getTimestamp().compareTo(c1.getTimestamp())))//sorts each list in the map by date in reverse
        .map(duplicatedEntries-> duplicatedEntries.get(0))//transform each list in a single ChangeEntry. The first one
        .filter(entry -> EXECUTED == entry.getState())//only gets the ones that are executed
        .sorted(Comparator.comparing(ChangeEntry::getTimestamp))// Sorts the resulting list chronologically
        .map(ExecutedChangeEntry::new)//transform the entry to an executed entry
        .collect(Collectors.toList());
  }

  /**
   * Returns all the changeEntries
   * @return
   */
  List<CHANGE_ENTRY> getAll();


  /**
   * If there is already an ChangeEntry the same executionId, id and author, it will be updated. Otherwise,
   * this method will be inserted.
   * @param changeEntry Entry to be inserted
   * @throws MongockException if any i/o exception or already inserted
   */
  void saveOrUpdate(CHANGE_ENTRY changeEntry) throws MongockException;

  void save(CHANGE_ENTRY changeEntry) throws MongockException;



}
