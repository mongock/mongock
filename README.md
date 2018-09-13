

[![CircleCI](https://img.shields.io/circleci/project/github/RedSparr0w/node-csgo-parser.svg)](https://circleci.com/gh/dieppa/mongock/tree/master.svg?style=svg&circle-token=6fbf4192bdae44715f54365502aa2f7bfcc47d33) [![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](https://github.com/dieppa/mongock/blob/master/LICENSE)

<p align="center">
    <img src="https://raw.githubusercontent.com/cloudyrock/mongock/master/misc/logo.png" width="200" />
</p>

# Mongock: MongoDB version control tool for Java

**mongock** is a java MongoDB tool for tracking, managing and applying database schema changes, initially forked from Mongobee project. 

The motivation of this project is to add some important and useful features, provide a different code design, which we believe is easier to maintain, extend and debug, 
applying what we believe are best practices, and specially providing a more fluent maintenance to apply collaborator's contributions.
The concept is very similar to other db migration tools such as [Liquibase](http://www.liquibase.org) or [Flyway](http://flywaydb.org) but *without using XML/JSON/YML files*.


**mongock** provides new approach for adding changes (change sets) based on Java classes and methods with appropriate annotations.

## Getting started

### Add a dependency

Mongock can be used standalone, with Spring, or with Jongo.  The `mongock-core` dependency is always required,
and _either_ `mongock-spring` or `mongock-jongo` can also be added.  Using `mongock-spring` with `mongock-jongo`
is not currently supported.

##### With Maven
```xml
<!-- To use standalone (i.e., w/o Spring or Jongo) -->
<dependency>
  <groupId>com.github.cloudyrock</groupId>
  <artifactId>mongock-core</artifactId>
  <version>1.15</version>
</dependency>

<!-- Add to use with Spring-->
<dependency>
  <groupId>com.github.cloudyrock</groupId>
  <artifactId>mongock-spring</artifactId>
  <version>1.15</version>
</dependency>


<!-- Add to use with Jongo -->
<dependency>
  <groupId>com.github.cloudyrock</groupId>
  <artifactId>mongock-jongo</artifactId>
  <version>1.15</version>
</dependency>

```
##### With Gradle
```groovy
compile 'org.javassist:javassist:3.18.2-GA'          // workround for ${javassist.version} placeholder issue*
compile 'com.github.cloudyrock:mongock-core:1.15'    // standalone
compile 'com.github.cloudyrock:mongock-spring:1.15'  // with Spring (in addition to mongock-core)
compile 'com.github.cloudyrock:mongock-jongo:1.15'   // with Jongo (in addition to mongock-core
```

### Usage with Spring

You need to instantiate mongock object and provide some configuration.
If you use Spring, mongock can be instantiated as a singleton bean in the Spring context. 
In this case the migration process will be executed automatically on startup.

```java
@Bean
public SpringMongock mongock() {
  MongoClient mongoclient = new MongoClient(new MongoClientURI("yourDbName", yourMongoClientBuilder));
  return new SpringMongockBuilder(mongoclient, "yourDbName", "com.package.to.be.scanned.for.changesets")
      .setLockQuickConfig()
      .build();
}
```

### Usage with Jongo

Using mongock with Jongo is similar, but you have to remember to run `execute` to start the migration process.

```java

  MongoClient mongoclient = new MongoClient(new MongoClientURI("yourDbName", yourMongoClientBuilder));
  JongoMongock runner=  new JongoMongockBuilder(mongoclient, "yourDbName", "com.package.to.be.scanned.for.changesets")
      .setLockQuickConfig()
      .setJongo(myJongo)
      .build();
  runner.execute();         //  ------> starts migration changesets
```



### Standalone usage
Using mongock standalone is similar to with Jongo.

```java

  MongoClient mongoclient = new MongoClient(new MongoClientURI("yourDbName", yourMongoClientBuilder));
  Mongock runner=  new MongockBuilder(mongoclient, "yourDbName", "com.package.to.be.scanned.for.changesets")
      .setLockQuickConfig()
      .build();
  runner.execute();         //  ------> starts migration changesets
```

Above examples provide minimal configuration. The various `Mongock` builders provide some other possibilities (setters) 
to make the tool more flexible:

```java
builder.setChangelogCollectionName(logColName);   // default is dbchangelog, collection with applied change sets
builder.setLockCollectionName(lockColName);       // default is mongocklock, collection used during migration process
builder.setEnabled(shouldBeEnabled);              // default is true, migration won't start if set to false
```

[More about URI](http://mongodb.github.io/mongo-java-driver/3.5/javadoc/)


### Creating change logs

`ChangeLog` contains bunch of `ChangeSet`s. `ChangeSet` is a single task (set of instructions made on a database). In 
other words `ChangeLog` is a class annotated with `@ChangeLog` and containing methods annotated with `@ChangeSet`.

```java 
package com.example.yourapp.changelogs;

@ChangeLog
public class DatabaseChangelog {
  
  @ChangeSet(order = "001", id = "someChangeId", author = "testAuthor")
  public void importantWorkToDo(DB db){
     // task implementation
  }


}
```
#### @ChangeLog

Class with change sets must be annotated by `@ChangeLog`. There can be more than one change log class but in that 
case `order` argument should be provided:

```java
@ChangeLog(order = "001")
public class DatabaseChangelog {
  //...
}
```
ChangeLogs are sorted alphabetically by `order` argument and changesets are applied due to this order.

#### @ChangeSet

Method annotated by @ChangeSet is taken and applied to the database. History of applied change sets is stored in a 
collection called `dbchangelog` (by default) in your MongoDB

##### Annotation parameters:

`order` - string for sorting change sets in one changelog. Sorting in alphabetical order, ascending. It can be a number, 
a date etc.

`id` - name of a change set, **must be unique** for all change logs in a database

`author` - author of a change set

`runAlways` - _[optional, default: false]_ changeset will always be executed but only first execution event will be 
stored in dbchangelog collection

##### Defining ChangeSet methods
Method annotated by `@ChangeSet` can have one of the following definition:

```java
@ChangeSet(order = "001", id = "someChangeWithoutArgs", author = "testAuthor")
public void someChange1() {
   // method without arguments can do some non-db changes
}

@ChangeSet(order = "002", id = "someChangeWithMongoDatabase", author = "testAuthor")
public void someChange2(MongoDatabase db) {
  // type: com.mongodb.client.MongoDatabase : original MongoDB driver v. 3.x, operations allowed by driver are possible
  // example: 
  MongoCollection<Document> mycollection = db.getCollection("mycollection");
  Document doc = new Document("testName", "example").append("test", "1");
  mycollection.insertOne(doc);
}

@ChangeSet(order = "003", id = "someChangeWithDb", author = "testAuthor")
public void someChange3(DB db) {
  // This is deprecated in mongo-java-driver 3.x, use MongoDatabase instead
  // type: com.mongodb.DB : original MongoDB driver v. 2.x, operations allowed by driver are possible
  // example: 
  DBCollection mycollection = db.getCollection("mycollection");
  BasicDBObject doc = new BasicDBObject().append("test", "1");
  mycollection .insert(doc);
}

@ChangeSet(order = "004", id = "someChangeWithJongo", author = "testAuthor")
public void someChange4(Jongo jongo) {
  // type: org.jongo.Jongo : Jongo driver can be used, used for simpler notation
  // example:
  MongoCollection mycollection = jongo.getCollection("mycollection");
  mycollection.insert("{test : 1}");
}

@ChangeSet(order = "005", id = "someChangeWithSpringDataTemplate", author = "testAuthor")
public void someChange5(MongoTemplate mongoTemplate) {
  // type: org.springframework.data.mongodb.core.MongoTemplate
  // Spring Data integration allows using MongoTemplate in the ChangeSet
  // example:
  mongoTemplate.save(myEntity);
}

@ChangeSet(order = "006", id = "someChangeWithSpringDataTemplate", author = "testAuthor")
public void someChange6(MongoTemplate mongoTemplate, Environment environment) {
  // type: org.springframework.data.mongodb.core.MongoTemplate
  // type: org.springframework.core.env.Environment
  // Spring Data integration allows using MongoTemplate and Environment in the ChangeSet
}
```

### Using Spring profiles
     
**mongock** accepts Spring's `org.springframework.context.annotation.Profile` annotation. If a change log or change set 
class is annotated  with `@Profile`, then it is activated for current application profiles.

_Example 1_: annotated change set will be invoked for a `dev` profile
```java
@Profile("dev")
@ChangeSet(author = "testuser", id = "myDevChangest", order = "01")
public void devEnvOnly(DB db){
  // ...
}
```
_Example 2_: all change sets in a changelog will be invoked for a `test` profile
```java
@ChangeLog(order = "1")
@Profile("test")
public class ChangelogForTestEnv{
  @ChangeSet(author = "testuser", id = "myTestChangest", order = "01")
  public void testingEnvOnly(DB db){
    // ...
  } 
}
```

#### Enabling @Profile annotation (option)
      
To enable the `@Profile` integration, please inject `org.springframework.core.env.Environment` to your runner.

```java      
@Bean 
@Autowired
public SpringMongock mongock(Environment environment) {
  SpringMongock runner = new SpringMongockBuilder(mongoclient, "yourDbName", "com.package.to.be.scanned.for.changesets")
      .setSpringEnvironment(environment)
      .setLockQuickConfig()
      .build();

  //... etc
}
```

#### Configuring Lock 
In order to execute the changelogs, mongock needs to manage the lock to ensure only one instance executes a changelog at a time.
By default the lock is reserved 24 hours and, in case the lock is held by another mongock instance, will ignore the execution
and no exception will be sent, unless the parameter throwExceptionIfCannotObtainLock is set to true.

There are 3 parameters to configure:

`lockAcquiredForMinutes` - Number of minutes mongock will acquire the lock for. It will refresh the lock when is close 
to be expired anyway. 

`maxTries` - Max tries when the lock is held by another mongock instance.

`maxWaitingForLockMinutes` - Max minutes mongock will wait for the lock in every try. 

To configure these parameters there are two methods: setLockConfig and `setLockConfig` and `setLockQuickConfig`. Both
 will set the parameter throwExceptionIfCannotObtainLock to true.
 ```java      
 @Bean @Autowired
 public Mongock mongock(Environment environment) {
   Mongock runner = new mongock(uri);
   runner.setLockConfig(5, 6, 3);
 }
 ```
 or quick config with 3 minutes for lockAcquiredFor, 3 max tries and 4 minutes for maxWaitingForLock
 ```java      
  @Bean @Autowired
  public Mongock mongock(Environment environment) {
    Mongock runner = new mongock(uri);
    runner.setLockQuickConfig();
  }
  ```
 
 

## Known issues

#### Mongo java driver conflicts

**mongock** depends on `mongo-java-driver`. If your application has mongo-java-driver dependency too, there could be 
library conflicts in some cases.

**Exception**:
```
com.mongodb.WriteConcernException: { "serverUsed" : "localhost" , 
"err" : "invalid ns to index" , "code" : 10096 , "n" : 0 , 
"connectionId" : 955 , "ok" : 1.0}
```

**Workaround**:

You can exclude mongo-java-driver from **mongock**  and use your dependency only. Maven and gradle examples below:
```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongo-java-driver</artifactId>
    <version>3.4.0</version>
</dependency>

<dependency>
  <groupId>com.github.cloudyrock</groupId>
  <artifactId>mongock-core</artifactId>
  <version>1.15</version>
  <exclusions>
    <exclusion>
      <groupId>org.mongodb</groupId>
      <artifactId>mongo-java-driver</artifactId>
    </exclusion>
  </exclusions>
</dependency>
```

```gradle
    // build.gradle
    compile "org.mongodb:mongo-java-driver:3.4.0"
    compile("com.github.cloudyrock:mongock:1.15") {
        exclude group: 'org.mongodb', module: 'mongo-java-driver'
    }

```

#### Mongo transaction limitations

Due to Mongo limitations, there is no way to provide atomicity at ChangelogSet level. So a Changelog could need 
more than one execution to be finished, as any interruption could happen, leaving the changelog in a inconsistent state.
If that happens, the next time mongock is executed it will try to finish the changelog execution, but it could already be 
half executed.

For this reason, the developer in charge of the changelog's design, should make sure that:
 
- **Changelog is idempotent**: As changelog can be interrupted at any time, it will need to be executed again. 
- **Changelog is Backward compatible (If high availability is required)**: While the migration process is taking place, 
the old version of the software is still running. During this time could happen(and probably will) that the old version 
of the software is dealing with the new version of the data. Could even happen that the data is a mix between old and 
new version. This means the software must still work regardless of the status of the database. In case the developer is aware of 
this and still decides to provide a non-backward-compatible changeSet, he should know it's a detriment to high 
availability.
- **Changelog reduces its execution time in every iteration**: This is harder to explain. As said, a changelog can be 
interrupted at any time. This means an specific changelog needs to be re-run. In the undesired scenario where the 
changelog's execution time is grater than the interruption time(could be Kubernetes initial delay), that changelog won't 
be ever finished. So the changelog needs to be developed in such a way that every iteration reduces its execution time, 
so eventually, after some iterations, the changelog finished. 
- **Changelog's execution time is shorter than interruption time**: In case the previous condition cannot be ensured, 
could be enough if the changelog's execution time is shorter than the interruption time. This is not ideal as the 
execution time depends on the machine, but in most case could be enough.
