package io.mongock.driver.remote.repository.external;

import io.mongock.driver.core.lock.LockStatus;

public class LockReqDto {
  private final String relativeKey;
  private final LockStatus status;
  private final long acquiredForMillis;
  private final boolean onlyExtension;

  public LockReqDto(String relativeKey, LockStatus status, long acquiredForMillis, boolean onlyExtension) {
    this.relativeKey = relativeKey;
    this.status = status;
    this.acquiredForMillis = acquiredForMillis;
    this.onlyExtension = onlyExtension;
  }

  public String getRelativeKey() {
    return relativeKey;
  }

  public LockStatus getStatus() {
    return status;
  }

  public long getAcquiredForMillis() {
    return acquiredForMillis;
  }

  public boolean isOnlyExtension() {
    return onlyExtension;
  }
}
