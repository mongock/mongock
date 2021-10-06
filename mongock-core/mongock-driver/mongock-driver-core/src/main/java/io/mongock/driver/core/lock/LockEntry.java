package io.mongock.driver.core.lock;

import io.mongock.utils.field.Field;

import java.util.Date;

/**
 * Type: entity class.
 *
 * @since 27/07/2014
 */
public class LockEntry {

  @Field("key")
  private final String key;

  @Field("status")
  private final String status;

  @Field("owner")
  private final String owner;

  @Field("expiresAt")
  private final Date expiresAt;

  public LockEntry(String key, String status, String owner, Date expiresAt) {
    this.key = key;
    this.status = status;
    this.owner = owner;
    this.expiresAt = expiresAt;
  }


  /**
   * @return lock's key
   */
  public String getKey() {
    return key;
  }

  /**
   * @return lock's status
   */
  public String getStatus() {
    return status;
  }

  /**
   * @return lock's owner
   */
  public String getOwner() {
    return owner;
  }

  /**
   * @return lock's expiration time
   * @see Date
   */
  public Date getExpiresAt() {
    return expiresAt;
  }

  /**
   * @param owner the owner to be checked
   * @return true if the parameter and the lock's owner are equals. False otherwise
   */
  public boolean isOwner(String owner) {
    return this.owner.equals(owner);
  }

  @Override
  public String toString() {
    return "LockEntry{" +
        "key='" + key + '\'' +
        ", status='" + status + '\'' +
        ", owner='" + owner + '\'' +
        ", expiresAt=" + expiresAt +
        '}';
  }
}



