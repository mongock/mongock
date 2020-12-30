package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.impl;

import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.ClientSessionDecorator;
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
