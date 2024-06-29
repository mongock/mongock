package io.mongock.driver.api.entry;

import java.util.Objects;

public class ChangeKey {

  private final String changeId;

  private final String author;

  public ChangeKey(String changeId,
                   String author) {
    this.changeId = changeId;
    this.author = author;
  }
  
  public ChangeKey(ChangeEntry entry) {
    this(entry.getChangeId(), entry.getAuthor());
  }

  public String getChangeId() {
    return this.changeId;
  }

  public String getAuthor() {
    return this.author;
  }

  @Override
  public String toString() {
    return "ChangeKey{" +
        "changeId='" + changeId + '\'' +
        ", author='" + author + '\'' +
        '}';
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 79 * hash + Objects.hashCode(this.changeId);
    hash = 79 * hash + Objects.hashCode(this.author);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ChangeKey other = (ChangeKey) obj;
    if (!Objects.equals(this.changeId, other.changeId)) {
      return false;
    }
    return Objects.equals(this.author, other.author);
  }
  
  
}
