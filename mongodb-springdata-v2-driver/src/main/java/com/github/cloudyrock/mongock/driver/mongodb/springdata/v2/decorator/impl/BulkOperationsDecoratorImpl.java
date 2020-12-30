package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.impl;

import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.BulkOperationsDecorator;
import org.springframework.data.mongodb.core.BulkOperations;

public class BulkOperationsDecoratorImpl implements BulkOperationsDecorator {

    private final BulkOperations impl;
    private final LockGuardInvoker invoker;

    public BulkOperationsDecoratorImpl(BulkOperations impl, LockGuardInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }
    @Override
    public BulkOperations getImpl() {
        return impl;
    }

    @Override
    public LockGuardInvoker getInvoker() {
        return invoker;
    }
}
