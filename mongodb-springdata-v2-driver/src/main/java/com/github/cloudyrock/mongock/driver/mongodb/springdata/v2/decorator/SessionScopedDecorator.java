package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator;

import com.github.cloudyrock.mongock.annotations.DecoratorDiverted;
import com.github.cloudyrock.mongock.annotations.NonLockGuarded;
import com.github.cloudyrock.mongock.annotations.NonLockGuardedType;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.impl.ClientSessionDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.impl.SessionCallbackDecoratorImpl;
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
