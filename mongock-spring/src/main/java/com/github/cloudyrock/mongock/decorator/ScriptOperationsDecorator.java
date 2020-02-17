package com.github.cloudyrock.mongock.decorator;

import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ScriptOperations;
import org.springframework.data.mongodb.core.script.ExecutableMongoScript;
import org.springframework.data.mongodb.core.script.NamedMongoScript;

import java.util.Set;

@Deprecated
public interface ScriptOperationsDecorator extends ScriptOperations {

    ScriptOperations getImpl();

    MethodInvoker getInvoker();


    @Override
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
