package io.mongock.driver.remote.repository.external;

import io.mongock.driver.core.lock.LockStatus;

public class LockResponseDto {
  private String organization;
  private String service;
  private String relativeKey;

  private String owner;
  private LockStatus status;
  private long acquiredForMillis;

  public LockResponseDto() {
  }

  public String getOrganization() {
    return organization;
  }

  public void setOrganization(String organization) {
    this.organization = organization;
  }

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }

  public String getRelativeKey() {
    return relativeKey;
  }

  public void setRelativeKey(String relativeKey) {
    this.relativeKey = relativeKey;
  }

  public LockStatus getStatus() {
    return status;
  }

  public void setStatus(LockStatus status) {
    this.status = status;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public long getAcquiredForMillis() {
    return acquiredForMillis;
  }

  public void setAcquiredForMillis(long acquiredForMillis) {
    this.acquiredForMillis = acquiredForMillis;
  }
}
