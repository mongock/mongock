package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator;

import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongoOperationsDecoratorImpl;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.SessionCallback;

public interface SessionCallbackDecorator<T> extends SessionCallback<T> {


    SessionCallback getImpl();

    LockGuardInvoker getInvoker();

    @Override
    default T doInSession(MongoOperations operations) {
        return getInvoker().invoke(()-> (T)getImpl().doInSession(new MongoOperationsDecoratorImpl(operations, getInvoker())));
    }
}
