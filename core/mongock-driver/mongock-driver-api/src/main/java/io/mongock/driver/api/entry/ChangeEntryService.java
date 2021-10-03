package io.mongock.driver.api.entry;

import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.common.RepositoryIndexable;
import io.mongock.utils.Process;

import java.util.List;


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
  List<ExecutedChangeEntry> getExecuted() throws MongockException;

  /**
   * If there is already an ChangeEntry the same executionId, id and author, it will be updated. Otherwise,
   * this method will be inserted.
   * @param changeEntry Entry to be inserted
   * @throws MongockException if any i/o exception or already inserted
   */
  void saveOrUpdate(CHANGE_ENTRY changeEntry) throws MongockException;

  void save(CHANGE_ENTRY changeEntry) throws MongockException;



}
