# Dependency upgrade considerations
This document tries to alert of some known considerations to have in mind when upgrading version in certain dependencies. Specially when they are critical.

## mongock-core
Dependency upgrade considerations for mongock-core module

### mongock-spring 

Relies on passing MongoTemplate itself as database accessor:
ScriptOperations
IndexOperations
BulkOperations
ExecutableFind
ExecutableUpdate
ExecutableRemove
ExecutableAggregation
ExecutableInsert
