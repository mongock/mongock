package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator;

import io.changock.migration.api.annotations.NonLockGuarded;
import io.changock.migration.api.annotations.NonLockGuardedType;
import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.ClientSessionDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.SessionCallbackDecoratorImpl;
import io.changock.migration.api.annotations.DecoratorDiverted;
import com.mongodb.client.ClientSession;
import org.springframework.data.mongodb.core.SessionCallback;
import org.springframework.data.mongodb.core.SessionScoped;

import java.util.function.Consumer;

public interface SessionScopedDecorator extends SessionScoped {

  SessionScoped getImpl();

  LockGuardInvoker getInvoker();

  @Override
  @DecoratorDiverted
  @NonLockGuarded(NonLockGuardedType.NONE)
  default <T> T execute(SessionCallback<T> sessionCallback, Consumer<ClientSession> clientSessionConsumer) {
    return getImpl().execute(
        new SessionCallbackDecoratorImpl<>(sessionCallback, getInvoker()),
        clientSession -> clientSessionConsumer.accept(new ClientSessionDecoratorImpl(clientSession, getInvoker())));
  }

  @Override
  @DecoratorDiverted
  @NonLockGuarded(NonLockGuardedType.NONE)
  default <T> T execute(SessionCallback<T> sessionCallback) {
    return getImpl().execute(new SessionCallbackDecoratorImpl<>(sessionCallback, getInvoker()), session -> { });
  }
}
