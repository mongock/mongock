package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.mapreduce;

import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.mapreduce.impl.ExecutableMapReduceDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;
import org.springframework.data.mongodb.core.mapreduce.MapReduceOptions;

public interface MapReduceWithOptionsDecorator<T> extends ExecutableMapReduceOperation.MapReduceWithOptions<T> {

    ExecutableMapReduceOperation.MapReduceWithOptions<T> getImpl();

    LockGuardInvoker getInvoker();

    //TODO implement
    @Override
    default ExecutableMapReduceOperation.ExecutableMapReduce<T> with(MapReduceOptions options) {
        return getInvoker().invoke(() -> new ExecutableMapReduceDecoratorImpl<>(getImpl().with(options), getInvoker()));
    }
}
