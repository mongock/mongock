package io.mongock.driver.mongodb.reactive;

import io.mongock.driver.mongodb.reactive.util.MongoSubscriberSync;
import io.mongock.driver.mongodb.reactive.util.FindPublisherMock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.containers.GenericContainer;

import java.util.Arrays;
import java.util.List;

public class DemoITest {
  public static GenericContainer mongo;


  private static final String MONGO_CONTAINER = "mongo:4.4.0";
  private static final Integer MONGO_PORT = 27017;
  @BeforeAll
  public static void createContainerForAll() {
    mongo = new GenericContainer(MONGO_CONTAINER).withExposedPorts(MONGO_PORT);
  }


  @Test
  public void test() {

    FindPublisherMock<String> spy = Mockito.spy(new FindPublisherMock<>(Arrays.asList("value1", "value2")));
    MongoSubscriberSync<String> subscriber = new MongoSubscriberSync<>();
    Mockito.doCallRealMethod().when(spy).subscribe(subscriber);
    spy.subscribe(subscriber);
    List<String> values = subscriber.get();

  }
}
