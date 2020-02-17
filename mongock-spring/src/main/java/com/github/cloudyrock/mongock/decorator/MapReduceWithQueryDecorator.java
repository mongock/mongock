package com.github.cloudyrock.mongock.decorator;

import com.github.cloudyrock.mongock.decorator.impl.TerminatingMapReduceDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;
import org.springframework.data.mongodb.core.query.Query;

public interface MapReduceWithQueryDecorator<T> extends ExecutableMapReduceOperation.MapReduceWithQuery<T>, TerminatingMapReduceDecorator<T> {


    @Override
    ExecutableMapReduceOperation.MapReduceWithQuery<T> getImpl();

    @Override
    default ExecutableMapReduceOperation.TerminatingMapReduce<T> matching(Query query) {
        return getInvoker().invoke(()-> new TerminatingMapReduceDecoratorImpl<>(getImpl().matching(query), getInvoker()));
    }

}
