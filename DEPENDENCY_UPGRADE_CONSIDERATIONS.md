# Dependency upgrade considerations
This document tries to alert of some known considerations to have in mind when upgrading version in certain dependencies. Specially when they are critical.


## mongo-java-driver
* When upgrading this dependency please take special care with `decorator` package. If you have a closer look, you will realise 
that we are using decorators for MongoDb accessors(MongoDatabase, MongoTemplate, etc). The goal is to ensure all the access to MongoDb
are under the umbrella of the distributed lock. However, some methods are safe accessed without previously checking the lock.
When you upgrade this dependency, you *must* ensure this conditions remains unaffected.

###### Specifically for `mongock-spring` take special attention to these classes:
- ScriptOperations
- IndexOperations
- BulkOperations
- ExecutableFind
- ExecutableUpdate
- ExecutableRemove
- ExecutableAggregation
- ExecutableInsert
