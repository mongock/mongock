package io.mongock.runner.core.builder.roles;

import io.mongock.api.config.MongockConfiguration;
import io.mongock.runner.core.builder.BuilderType;

public interface Typeable<SELF extends Typeable<SELF, CONFIG>, CONFIG extends MongockConfiguration>
    extends Configurable<SELF, CONFIG>, SelfInstanstiator<SELF> {

  BuilderType getType();

}
