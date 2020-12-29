package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.mapreduce.impl;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.mapreduce.MapReduceWithCollectionDecorator;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public class MapReduceWithCollectionDecoratorImpl<T> implements MapReduceWithCollectionDecorator<T> {

    private final ExecutableMapReduceOperation.MapReduceWithCollection<T> impl;
    private final LockGuardInvoker invoker;

    public MapReduceWithCollectionDecoratorImpl(ExecutableMapReduceOperation.MapReduceWithCollection<T> impl,
                                                LockGuardInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }
    @Override
    public ExecutableMapReduceOperation.MapReduceWithCollection<T> getImpl() {
        return impl;
    }

    @Override
    public LockGuardInvoker getInvoker() {
        return invoker;
    }
}
