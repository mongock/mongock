package io.mongock.driver.core.lock;

import io.mongock.utils.field.Field;

import java.util.Date;

import static io.mongock.utils.field.Field.KeyType.PRIMARY;

/**
 * Type: entity class.
 *
 * @since 27/07/2014
 */
public class LockEntry {
  public static final String KEY_FIELD = "key";
  public static final String STATUS_FIELD = "status";
  public static final String OWNER_FIELD = "owner";
  public static final String EXPIRES_AT_FIELD = "expiresAt";

  @Field(value = KEY_FIELD, type = PRIMARY)
  private final String key;

  @Field(STATUS_FIELD)
  private final String status;

  @Field(OWNER_FIELD)
  private final String owner;

  @Field(EXPIRES_AT_FIELD)
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

  public long getAcquiredForMillisFromNow() {
    return getExpiresAt().getTime() - new Date().getTime();
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



