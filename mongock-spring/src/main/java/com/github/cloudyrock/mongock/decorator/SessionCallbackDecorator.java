package com.github.cloudyrock.mongock.decorator;

import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.github.cloudyrock.mongock.decorator.impl.MongoOperationsDecoratorImpl;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.SessionCallback;

public interface SessionCallbackDecorator<T> extends SessionCallback<T> {


    SessionCallback getImpl();

    MethodInvoker getInvoker();

    @Override
    default T doInSession(MongoOperations operations) {
        return getInvoker().invoke(()-> (T)getImpl().doInSession(new MongoOperationsDecoratorImpl(operations, getInvoker())));
    }
}
