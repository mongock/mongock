package io.mongock.runner.springboot;


import io.mongock.runner.core.event.EventPublisher;
import io.mongock.runner.core.event.result.MigrationSuccessResult;
import io.mongock.runner.spring.base.events.SpringMigrationFailureEvent;
import io.mongock.runner.spring.base.events.SpringMigrationStartedEvent;
import io.mongock.runner.spring.base.events.SpringMigrationSuccessEvent;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

public class SpringEventPublisherTest {

  @Test
  public void shouldCallSuccessListener() {
    ApplicationEventPublisher applicationEventPublisher = Mockito.mock(ApplicationEventPublisher.class);
    new EventPublisher(
        () -> applicationEventPublisher.publishEvent(new SpringMigrationStartedEvent(this)),
        result -> applicationEventPublisher.publishEvent(new SpringMigrationSuccessEvent(this, result)),
        result -> applicationEventPublisher.publishEvent(new SpringMigrationFailureEvent(this, result))
    ).publishMigrationSuccessEvent(new MigrationSuccessResult(true));

    ArgumentCaptor<SpringMigrationSuccessEvent> eventCaptor = ArgumentCaptor.forClass(SpringMigrationSuccessEvent.class);
    verify(applicationEventPublisher, new Times(1)).publishEvent(eventCaptor.capture());
    Assert.assertTrue(eventCaptor.getValue().getMigrationResult().isSuccess());

  }

  @Test
  public void shouldCallFailListener() {
    RuntimeException ex = new RuntimeException();
    ApplicationEventPublisher applicationEventPublisher = Mockito.mock(ApplicationEventPublisher.class);
    new EventPublisher(
        () -> applicationEventPublisher.publishEvent(new SpringMigrationStartedEvent(this)),
        result -> applicationEventPublisher.publishEvent(new SpringMigrationSuccessEvent(this, result)),
        result -> applicationEventPublisher.publishEvent(new SpringMigrationFailureEvent(this, result))
    ).publishMigrationFailedEvent(ex);

    ArgumentCaptor<SpringMigrationFailureEvent> eventCaptor = ArgumentCaptor.forClass(SpringMigrationFailureEvent.class);
    verify(applicationEventPublisher, new Times(1)).publishEvent(eventCaptor.capture());
    assertEquals(ex, eventCaptor.getValue().getMigrationResult().getException());
  }


}
