# Dependency upgrade considerations
This document tries to alert of some known considerations to have in mind when upgrading version in certain dependencies. Specially when they are critical.


## mongo-java-driver
* When upgrading this dependency please take special care with `decorator` package. If you have a closer look you will realise 
that we are using decorators for MongoDatabaseTemplate. The objective of this is to make sure all the access to MongoDb
are under the umbrella of the distributed lock. However, some methods are safe without previously checking the lock.
When you upgrade this dependency you *must* ensure this conditions stays unaffected.

###### Specifically for `mongock-spring` take special attention to these classes:
- ScriptOperations
- IndexOperations
- BulkOperations
- ExecutableFind
- ExecutableUpdate
- ExecutableRemove
- ExecutableAggregation
- ExecutableInsert
