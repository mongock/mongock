package io.mongock.runner.core.executor.changelog;

import io.mongock.runner.core.internal.ChangeSetItem;

import java.io.Serializable;
import java.util.Comparator;

public class ChangeSetComparator<CHANGESET extends ChangeSetItem> implements Comparator<CHANGESET>, Serializable {
  private static final long serialVersionUID = -854690868262484102L;

  @Override
  public int compare(CHANGESET c1, CHANGESET c2) {
    return c1.getOrder().compareTo(c2.getOrder());
  }
}
