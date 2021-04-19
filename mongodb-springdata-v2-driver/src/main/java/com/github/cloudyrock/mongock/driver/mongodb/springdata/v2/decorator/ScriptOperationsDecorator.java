package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator;

import com.github.cloudyrock.mongock.NonLockGuarded;
import com.github.cloudyrock.mongock.NonLockGuardedType;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ScriptOperations;
import org.springframework.data.mongodb.core.script.ExecutableMongoScript;
import org.springframework.data.mongodb.core.script.NamedMongoScript;

import java.util.Set;

@Deprecated
public interface ScriptOperationsDecorator extends ScriptOperations {

    ScriptOperations getImpl();

    LockGuardInvoker getInvoker();


    @Override
    @NonLockGuarded(NonLockGuardedType.NONE)
    default NamedMongoScript register(ExecutableMongoScript script) {
        return getInvoker().invoke(()-> getImpl().register(script));
    }

    @Override
    default NamedMongoScript register(NamedMongoScript script) {
        return getInvoker().invoke(()-> getImpl().register(script));
    }

    @Override
    default Object execute(ExecutableMongoScript script, Object... args) {
        return getInvoker().invoke(()-> getImpl().execute(script, args));
    }

    @Override
    default Object call(String scriptName, Object... args) {
        return getInvoker().invoke(()-> getImpl().call(scriptName, args));
    }

    @Override
    default boolean exists(String scriptName) {
        return getInvoker().invoke(()-> getImpl().exists(scriptName));
    }

    @Override
    default Set<String> getScriptNames() {
        return getInvoker().invoke(()-> getImpl().getScriptNames());
    }
}
