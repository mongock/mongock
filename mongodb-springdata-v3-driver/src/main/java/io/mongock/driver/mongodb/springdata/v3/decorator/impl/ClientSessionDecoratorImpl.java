package io.mongock.driver.mongodb.springdata.v3.decorator.impl;

import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.mongock.driver.mongodb.springdata.v3.decorator.ClientSessionDecorator;
import com.mongodb.client.ClientSession;

public class ClientSessionDecoratorImpl implements ClientSessionDecorator {

    private final ClientSession impl;
    private final LockGuardInvoker invoker;

    public ClientSessionDecoratorImpl(ClientSession impl, LockGuardInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }
    @Override
    public ClientSession getImpl() {
        return impl;
    }

    @Override
    public LockGuardInvoker getInvoker() {
        return invoker;
    }
}
