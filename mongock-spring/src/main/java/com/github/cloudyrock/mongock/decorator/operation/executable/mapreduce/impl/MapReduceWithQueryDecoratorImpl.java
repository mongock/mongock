package com.github.cloudyrock.mongock.decorator.operation.executable.mapreduce.impl;

import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.github.cloudyrock.mongock.decorator.operation.executable.mapreduce.MapReduceWithQueryDecorator;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public class MapReduceWithQueryDecoratorImpl<T> implements MapReduceWithQueryDecorator<T> {

    private final MethodInvoker invoker;
    private final ExecutableMapReduceOperation.MapReduceWithQuery<T> impl;

    public MapReduceWithQueryDecoratorImpl(ExecutableMapReduceOperation.MapReduceWithQuery<T> impl, MethodInvoker methodInvoker) {
        this.impl = impl;
        this.invoker = methodInvoker;
    }

    @Override
    public ExecutableMapReduceOperation.MapReduceWithQuery<T> getImpl() {
        return impl;
    }

    @Override
    public MethodInvoker getInvoker() {
        return invoker;
    }
}
