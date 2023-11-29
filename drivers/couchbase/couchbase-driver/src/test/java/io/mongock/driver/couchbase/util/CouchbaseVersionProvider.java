package io.mongock.driver.couchbase.util;

import io.mongock.driver.couchbase.TestcontainersCouchbaseRunner;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class CouchbaseVersionProvider {
    static Stream<Arguments> versions() {
        return Stream.of(
                Arguments.of(TestcontainersCouchbaseRunner.getBucketV6(), "6.6.0"),
                Arguments.of(TestcontainersCouchbaseRunner.getBucketV7(), "7.1.1")
        );
    }

    static Stream<Arguments> versions2() {
        return Stream.of(
                Arguments.of(TestcontainersCouchbaseRunner.getBucketV6(), false),
                Arguments.of(TestcontainersCouchbaseRunner.getBucketV7(), true)
        );
    }
}
