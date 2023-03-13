package io.mongock.driver.couchbase.util;

import com.couchbase.client.java.Bucket;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

class CouchbaseVersionUtilTest {
  @ParameterizedTest
  @DisplayName("getCouchbaseServerVersion: couchbase version should match")
  @MethodSource("io.mongock.driver.couchbase.util.CouchbaseVersionProvider#versions")
  void test_get_version(Bucket bucket, String expectedVersion) {
    // when
    String couchbaseVersion = CouchbaseVersionUtil.getCouchbaseServerVersion(bucket);
    
    // then
    assertThat(couchbaseVersion, CoreMatchers.startsWith(expectedVersion));
  }

  @ParameterizedTest
  @DisplayName("is7andUp: couchbase version is 7 and up")
  @MethodSource("io.mongock.driver.couchbase.util.CouchbaseVersionProvider#versions2")
  void test_get_version(Bucket bucket, boolean expectedSevenAndUp) {
    // when
    boolean sevenAndUp = CouchbaseVersionUtil.is7andUp(bucket);

    // then
    assertEquals(expectedSevenAndUp, sevenAndUp);
  }
}
