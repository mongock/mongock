package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.mapreduce.impl;

import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.mapreduce.ExecutableMapReduceDecorator;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public class ExecutableMapReduceDecoratorImpl<T> implements ExecutableMapReduceDecorator<T> {

    private final ExecutableMapReduceOperation.ExecutableMapReduce<T> impl;
    private final LockGuardInvoker invoker;

    public ExecutableMapReduceDecoratorImpl(ExecutableMapReduceOperation.ExecutableMapReduce<T> impl, LockGuardInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }

    @Override
    public ExecutableMapReduceOperation.ExecutableMapReduce<T> getImpl() {
        return impl;
    }

    @Override
    public LockGuardInvoker getInvoker() {
        return invoker;
    }
}
