package com.github.cloudyrock.mongock.decorator;

import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.github.cloudyrock.mongock.decorator.impl.MapReduceWithMapFunctionDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public interface ExecutableMapReduceOperationDecorator extends ExecutableMapReduceOperation {

    ExecutableMapReduceOperation getImpl();

    MethodInvoker getInvoker();

    @Override
    default <T> MapReduceWithMapFunction<T> mapReduce(Class<T> domainType) {
        return getInvoker().invoke(()-> new MapReduceWithMapFunctionDecoratorImpl<>(getImpl().mapReduce(domainType), getInvoker()));
    }
}
