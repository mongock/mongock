package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.mapreduce.impl;

import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.mapreduce.MapReduceWithOptionsDecorator;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public class MapReduceWithOptionsDecoratorImpl<T> implements MapReduceWithOptionsDecorator<T> {

    private final ExecutableMapReduceOperation.MapReduceWithOptions<T> impl;
    private final LockGuardInvoker invoker;

    public MapReduceWithOptionsDecoratorImpl(ExecutableMapReduceOperation.MapReduceWithOptions<T> impl, LockGuardInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }

    @Override
    public ExecutableMapReduceOperation.MapReduceWithOptions<T> getImpl() {
        return impl;
    }

    @Override
    public LockGuardInvoker getInvoker() {
        return invoker;
    }
}
