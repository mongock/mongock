package com.github.cloudyrock.mongock.decorator;

import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.github.cloudyrock.mongock.decorator.impl.ExecutableMapReduceDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;
import org.springframework.data.mongodb.core.mapreduce.MapReduceOptions;

public interface MapReduceWithOptionsDecorator<T> extends ExecutableMapReduceOperation.MapReduceWithOptions<T> {

    ExecutableMapReduceOperation.MapReduceWithOptions<T> getImpl();

    MethodInvoker getInvoker();

    //TODO implement
    @Override
    default ExecutableMapReduceOperation.ExecutableMapReduce<T> with(MapReduceOptions options) {
        return getInvoker().invoke(() -> new ExecutableMapReduceDecoratorImpl<>(getImpl().with(options), getInvoker()));
    }
}
