package com.github.cloudyrock.mongock.decorator;

import com.github.cloudyrock.mongock.decorator.util.MongockDecoratorBase;
import org.springframework.data.mongodb.core.ScriptOperations;
import org.springframework.data.mongodb.core.script.ExecutableMongoScript;
import org.springframework.data.mongodb.core.script.NamedMongoScript;

import java.util.Set;

public interface ScriptOperationsDecorator extends ScriptOperations, MongockDecoratorBase<ScriptOperations> {
  
  
  
  @Override
  default NamedMongoScript register(ExecutableMongoScript script) {
    return null;
  }

  @Override
  default NamedMongoScript register(NamedMongoScript script) {
    return null;
  }

  @Override
  default Object execute(ExecutableMongoScript script, Object... args) {
    return null;
  }

  @Override
  default Object call(String scriptName, Object... args) {
    return null;
  }

  @Override
  default boolean exists(String scriptName) {
    return false;
  }

  @Override
  default Set<String> getScriptNames() {
    return null;
  }
}
