package com.github.cloudyrock.mongock.decorator;

import com.mongodb.client.ClientSession;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.github.cloudyrock.mongock.decorator.impl.ClientSessionDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.impl.SessionCallbackDecoratorImpl;
import org.springframework.data.mongodb.core.SessionCallback;
import org.springframework.data.mongodb.core.SessionScoped;

import java.util.function.Consumer;

public interface SessionScopedDecorator extends SessionScoped {

    SessionScoped getImpl();

    MethodInvoker getInvoker();

    @Override
    default  <T> T execute(SessionCallback<T> action, Consumer<ClientSession> doFinally) {
        SessionCallback<T> sessionCallback = new SessionCallbackDecoratorImpl<>(action, getInvoker());
        Consumer<ClientSession> consumer = clientSession -> {
            ClientSession clientSessionDecorator = new ClientSessionDecoratorImpl(clientSession, getInvoker());
            doFinally.accept(clientSessionDecorator);
        };
        return getInvoker().invoke(()->
            getImpl().execute(sessionCallback, consumer));
    }
}
