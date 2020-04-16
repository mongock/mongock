package com.github.cloudyrock.mongock.decorator.operation.executable.remove;

import com.github.cloudyrock.mongock.decorator.util.Invokable;
import org.springframework.data.mongodb.core.ExecutableRemoveOperation;

public interface ExecutableRemoveDecorator<T> extends Invokable, ExecutableRemoveOperation.ExecutableRemove<T>, RemoveWithCollectionDecorator<T> {
}
