package com.github.cloudyrock.mongock.decorator.operation.executable.mapreduce.impl;

import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.github.cloudyrock.mongock.decorator.operation.executable.mapreduce.MapReduceWithOptionsDecorator;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public class MapReduceWithOptionsDecoratorImpl<T> implements MapReduceWithOptionsDecorator<T> {

    private final ExecutableMapReduceOperation.MapReduceWithOptions<T> impl;
    private final MethodInvoker invoker;

    public MapReduceWithOptionsDecoratorImpl(ExecutableMapReduceOperation.MapReduceWithOptions<T> impl, MethodInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }

    @Override
    public ExecutableMapReduceOperation.MapReduceWithOptions<T> getImpl() {
        return impl;
    }

    @Override
    public MethodInvoker getInvoker() {
        return invoker;
    }
}
