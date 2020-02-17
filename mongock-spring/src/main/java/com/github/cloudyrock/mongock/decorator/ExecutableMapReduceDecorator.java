package com.github.cloudyrock.mongock.decorator;

import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public interface ExecutableMapReduceDecorator<T> extends ExecutableMapReduceOperation.ExecutableMapReduce<T>,
        MapReduceWithMapFunctionDecorator<T>, MapReduceWithReduceFunctionDecorator<T>,
        MapReduceWithCollectionDecorator<T>, MapReduceWithProjectionDecorator<T>, MapReduceWithOptionsDecorator<T> {
    @Override
    ExecutableMapReduceOperation.ExecutableMapReduce<T> getImpl();

}
