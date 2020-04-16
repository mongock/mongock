package com.github.cloudyrock.mongock.decorator.operation.executable.mapreduce;

import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.github.cloudyrock.mongock.decorator.operation.executable.mapreduce.impl.ExecutableMapReduceDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public interface MapReduceWithReduceFunctionDecorator<T> extends ExecutableMapReduceOperation.MapReduceWithReduceFunction<T> {

    ExecutableMapReduceOperation.MapReduceWithReduceFunction<T> getImpl();

    MethodInvoker getInvoker();

    //TODO IMPLEMENT THIS DECORATOR
    @Override
    default ExecutableMapReduceOperation.ExecutableMapReduce<T> reduce(String reduceFunction) {
        return getInvoker().invoke(() -> new ExecutableMapReduceDecoratorImpl<>(getImpl().reduce(reduceFunction), getInvoker()));
    }
}
