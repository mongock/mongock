package io.mongock.driver.couchbase.util;

import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.driver.api.entry.ChangeState;
import io.mongock.driver.api.entry.ChangeType;
import io.mongock.driver.couchbase.TestcontainersCouchbaseRunner;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Stream;

public class ChangeEntryProvider {
  private static final ChangeEntry CHANGE_1 = new ChangeEntry(
      "executionId",
      "c1",
      "author-c1",
      new Date(),
      ChangeState.EXECUTED,
      ChangeType.EXECUTION,
      "changeLogClass",
      "changeSetMethod",
      0L,
      "executionHostname",
      Collections.singletonMap("this", "that"),
      false
  );

  private static final ChangeEntry CHANGE_1_U = new ChangeEntry(
      "executionId",
      "c1",
      "author-c1",
      new Date(),
      ChangeState.EXECUTED,
      ChangeType.EXECUTION,
      "UPDATED",
      "UPDATED",
      0L,
      "UPDATED",
      Collections.singletonMap("UPDATED", "that"),
      false
  );

  private static final ChangeEntry CHANGE_2 = new ChangeEntry(
      "executionId",
      "c2",
      "author-c2",
      new Date(),
      ChangeState.EXECUTED,
      ChangeType.EXECUTION,
      "changeLogClass",
      "changeSetMethod",
      0L,
      "executionHostname",
      Collections.singletonMap("this", "that"),
      false
  );

  private static final ChangeEntry CHANGE_FAILED = new ChangeEntry(
      "executionId",
      "c-failed",
      "author-c3",
      new Date(),
      ChangeState.EXECUTED,
      ChangeType.EXECUTION,
      "changeLogClass",
      "changeSetMethod",
      0L,
      "executionHostname",
      Collections.singletonMap("this", "that"),
      "some error",
      false
  );

  private static final ChangeEntry CHANGE_3 = new ChangeEntry(
      "executionId",
      "c3",
      "author-c3",
      new Date(),
      ChangeState.EXECUTED,
      ChangeType.EXECUTION,
      "changeLogClass",
      "changeSetMethod",
      0L,
      "executionHostname",
      Collections.singletonMap("this", "that"),
      false
  );

  static Stream<Arguments> changes() {
    return Stream.of(
        Arguments.of(TestcontainersCouchbaseRunner.getCluster6(), TestcontainersCouchbaseRunner.getCollectionV6(), CHANGE_1),
        Arguments.of(TestcontainersCouchbaseRunner.getCluster6(), TestcontainersCouchbaseRunner.getCollectionV6(), CHANGE_2),
        Arguments.of(TestcontainersCouchbaseRunner.getCluster6(), TestcontainersCouchbaseRunner.getCollectionV6(), CHANGE_3),
        Arguments.of(TestcontainersCouchbaseRunner.getCluster6(), TestcontainersCouchbaseRunner.getCollectionV6(), CHANGE_FAILED),
        Arguments.of(TestcontainersCouchbaseRunner.getCluster7(), TestcontainersCouchbaseRunner.getCollectionV7(), CHANGE_1),
        Arguments.of(TestcontainersCouchbaseRunner.getCluster7(), TestcontainersCouchbaseRunner.getCollectionV7(), CHANGE_2),
        Arguments.of(TestcontainersCouchbaseRunner.getCluster7(), TestcontainersCouchbaseRunner.getCollectionV7(), CHANGE_3),
        Arguments.of(TestcontainersCouchbaseRunner.getCluster7(), TestcontainersCouchbaseRunner.getCollectionV7(), CHANGE_FAILED)
    );
  }

  static Stream<Arguments> changesAsList() {
    return Stream.of(
        Arguments.of(TestcontainersCouchbaseRunner.getCluster6(), TestcontainersCouchbaseRunner.getCollectionV6(), Arrays.asList(CHANGE_1, CHANGE_2, CHANGE_3, CHANGE_FAILED)),
        Arguments.of(TestcontainersCouchbaseRunner.getCluster7(), TestcontainersCouchbaseRunner.getCollectionV7(), Arrays.asList(CHANGE_1, CHANGE_2, CHANGE_3, CHANGE_FAILED))
    );
  }

  static Stream<Arguments> change1AndChange1U() {
    return Stream.of(
        Arguments.of(TestcontainersCouchbaseRunner.getCluster6(), TestcontainersCouchbaseRunner.getCollectionV6(), CHANGE_1, CHANGE_1_U),
        Arguments.of(TestcontainersCouchbaseRunner.getCluster7(), TestcontainersCouchbaseRunner.getCollectionV7(), CHANGE_1, CHANGE_1_U)
    );
  }
}
