package com.github.cloudyrock.mongock;

/**
 *
 * @since 27/07/2014
 */
public interface ChangeEntryRepository extends Repository{

  boolean isNewChange(ChangeEntry changeEntry) throws MongockException;

  void save(ChangeEntry changeEntry) throws MongockException;


}
