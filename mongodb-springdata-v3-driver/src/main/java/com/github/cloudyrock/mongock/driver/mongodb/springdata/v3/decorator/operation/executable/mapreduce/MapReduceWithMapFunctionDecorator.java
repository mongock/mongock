package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.mapreduce;

import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.mapreduce.impl.MapReduceWithReduceFunctionDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public interface MapReduceWithMapFunctionDecorator<T> extends ExecutableMapReduceOperation.MapReduceWithMapFunction<T> {

    ExecutableMapReduceOperation.MapReduceWithMapFunction<T> getImpl();

    LockGuardInvoker getInvoker();

    @Override
    default ExecutableMapReduceOperation.MapReduceWithReduceFunction<T> map(String mapFunction) {
        return getInvoker().invoke(()-> new MapReduceWithReduceFunctionDecoratorImpl<>(getImpl().map(mapFunction), getInvoker()));
    }
}
