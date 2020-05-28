package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl;

import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.SessionCallbackDecorator;
import org.springframework.data.mongodb.core.SessionCallback;

public class SessionCallbackDecoratorImpl<T> implements SessionCallbackDecorator<T> {

    private final SessionCallback<T> impl;
    private final LockGuardInvoker invoker;

    public SessionCallbackDecoratorImpl(SessionCallback<T> impl, LockGuardInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }
    @Override
    public SessionCallback getImpl() {
        return impl;
    }

    @Override
    public LockGuardInvoker getInvoker() {
        return invoker;
    }
}
