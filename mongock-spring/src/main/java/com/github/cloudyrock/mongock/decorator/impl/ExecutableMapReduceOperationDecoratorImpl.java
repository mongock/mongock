package com.github.cloudyrock.mongock.decorator.impl;

import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.github.cloudyrock.mongock.decorator.ExecutableMapReduceOperationDecorator;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public class ExecutableMapReduceOperationDecoratorImpl implements ExecutableMapReduceOperationDecorator {

    private final ExecutableMapReduceOperation impl;
    private final MethodInvoker invoker;

    public ExecutableMapReduceOperationDecoratorImpl(ExecutableMapReduceOperation impl, MethodInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }

    @Override
    public ExecutableMapReduceOperation getImpl() {
        return impl;
    }

    @Override
    public MethodInvoker getInvoker() {
        return invoker;
    }
}
