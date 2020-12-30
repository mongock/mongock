package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.mapreduce;

import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.mapreduce.impl.ExecutableMapReduceDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public interface MapReduceWithReduceFunctionDecorator<T> extends ExecutableMapReduceOperation.MapReduceWithReduceFunction<T> {

    ExecutableMapReduceOperation.MapReduceWithReduceFunction<T> getImpl();

    LockGuardInvoker getInvoker();

    //TODO IMPLEMENT THIS DECORATOR
    @Override
    default ExecutableMapReduceOperation.ExecutableMapReduce<T> reduce(String reduceFunction) {
        return getInvoker().invoke(() -> new ExecutableMapReduceDecoratorImpl<>(getImpl().reduce(reduceFunction), getInvoker()));
    }
}
