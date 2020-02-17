package com.github.cloudyrock.mongock.decorator.impl;

import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.github.cloudyrock.mongock.decorator.SessionCallbackDecorator;
import org.springframework.data.mongodb.core.SessionCallback;

public class SessionCallbackDecoratorImpl<T> implements SessionCallbackDecorator<T> {

    private final SessionCallback<T> impl;
    private final MethodInvoker invoker;

    public SessionCallbackDecoratorImpl(SessionCallback<T> impl, MethodInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }
    @Override
    public SessionCallback getImpl() {
        return impl;
    }

    @Override
    public MethodInvoker getInvoker() {
        return invoker;
    }
}
