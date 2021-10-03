package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.impl;

import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.ScriptOperationsDecorator;
import org.springframework.data.mongodb.core.ScriptOperations;

@Deprecated
public class ScriptOperationsDecoratorImpl implements ScriptOperationsDecorator {

    private final ScriptOperations impl;
    private final LockGuardInvoker invoker;

    public ScriptOperationsDecoratorImpl(ScriptOperations impl, LockGuardInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }
    @Override
    public ScriptOperations getImpl() {
        return impl;
    }

    @Override
    public LockGuardInvoker getInvoker() {
        return invoker;
    }
}
