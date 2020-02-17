package com.github.cloudyrock.mongock.decorator.impl;

import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.github.cloudyrock.mongock.decorator.IndexOperationsProviderDecorator;
import org.springframework.data.mongodb.core.index.IndexOperationsProvider;

public class IndexOperationsProviderDecoratorImpl implements IndexOperationsProviderDecorator {

    private final IndexOperationsProvider impl;
    private final MethodInvoker invoker;

    public IndexOperationsProviderDecoratorImpl(IndexOperationsProvider impl, MethodInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }

    @Override
    public IndexOperationsProvider getimpl() {
        return impl;
    }

    @Override
    public MethodInvoker getInvoker() {
        return invoker;
    }
}
