package io.mongock.runner.core.executor.changelog;

import io.mongock.runner.core.internal.ChangeLogItem;
import io.mongock.utils.StringUtils;

import java.io.Serializable;
import java.util.Comparator;

class ChangeLogComparator implements Comparator<ChangeLogItem>, Serializable {
  private static final long serialVersionUID = -358162121872177974L;



  /**
   * if order1 and order2 are not null and different, it return their compare. If one of then is null, the other is first.
   * If both are null or equals, they are compare by their names
   */
  @Override
  public int compare(ChangeLogItem changeLog1, ChangeLogItem changeLog2) {
    String val1 = changeLog1.getOrder();
    String val2 = changeLog2.getOrder();
    if(changeLog1.isSystem() && !changeLog2.isSystem()) {
      return -1;
    } else if(changeLog2.isSystem() && !changeLog1.isSystem()) {
      return 1;
    }else if (StringUtils.hasText(val1) && StringUtils.hasText(val2) && !val1.equals(val2)) {
      return val1.compareTo(val2);
    } else if (StringUtils.hasText(val1) && !StringUtils.hasText(val2)) {
      return -1;
    } else if (StringUtils.hasText(val2) && !StringUtils.hasText(val1)) {
      return 1;
    } else {
      return changeLog1.getType().getCanonicalName().compareTo(changeLog2.getType().getCanonicalName());
    }

  }
}
