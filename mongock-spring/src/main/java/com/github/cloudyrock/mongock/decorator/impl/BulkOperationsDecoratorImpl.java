package com.github.cloudyrock.mongock.decorator.impl;

import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.github.cloudyrock.mongock.decorator.BulkOperationsDecorator;
import org.springframework.data.mongodb.core.BulkOperations;

public class BulkOperationsDecoratorImpl implements BulkOperationsDecorator {

    private final BulkOperations impl;
    private final MethodInvoker invoker;

    public BulkOperationsDecoratorImpl(BulkOperations impl, MethodInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }
    @Override
    public BulkOperations getImpl() {
        return impl;
    }

    @Override
    public MethodInvoker getInvoker() {
        return invoker;
    }
}
