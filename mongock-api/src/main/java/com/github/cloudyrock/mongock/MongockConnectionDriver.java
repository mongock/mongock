package com.github.cloudyrock.mongock;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.driver.api.entry.ChangeEntry;

public interface MongockConnectionDriver<CHANGE_ENTRY extends ChangeEntry> extends ConnectionDriver<CHANGE_ENTRY> {

  void setChangeLogCollectionName(String changeLogCollectionName);

  String getChangeLogCollectionName();

  void setLockCollectionName(String lockCollectionName);

  String getLockCollectionName();

  void setIndexCreation(boolean indexCreation);
}
