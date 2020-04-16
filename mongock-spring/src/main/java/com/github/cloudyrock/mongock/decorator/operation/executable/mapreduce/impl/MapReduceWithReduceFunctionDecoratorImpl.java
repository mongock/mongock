package com.github.cloudyrock.mongock.decorator.operation.executable.mapreduce.impl;

import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.github.cloudyrock.mongock.decorator.operation.executable.mapreduce.MapReduceWithReduceFunctionDecorator;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public class MapReduceWithReduceFunctionDecoratorImpl<T> implements MapReduceWithReduceFunctionDecorator<T> {

    private final ExecutableMapReduceOperation.MapReduceWithReduceFunction<T> impl;
    private final MethodInvoker invoker;

    public MapReduceWithReduceFunctionDecoratorImpl(ExecutableMapReduceOperation.MapReduceWithReduceFunction<T> impl,
                                                    MethodInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }
    @Override
    public ExecutableMapReduceOperation.MapReduceWithReduceFunction<T> getImpl() {
        return impl;
    }

    @Override
    public MethodInvoker getInvoker() {
        return invoker;
    }
}
