# Version 5 upgrade steps

### Scope
This document is a temporally guide to upgrade your current project from using Mongock version 4.x.x to 5.0.x.RC, while the official documentation is released,
where you will find a proper guide to have your project running with Mongock 5, examples, explanation for our decisions and how they can benefit you, etc.

### Pom changes
- groupId has change from `com.github.cloudyrock.mongock` to `io.mongock`
- BOM version to latest version: [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.mongock/mongock/badge.png)](https://search.maven.org/artifact/io.mongock/mongock-driver-mongodb)

```xml
<dependencyManagement>
   <dependencies>
       <dependency>
           <groupId>io.mongock</groupId>
           <artifactId>mongock-bom</artifactId>
           <version>${latest_version}</version>
           <type>pom</type>
           <scope>import</scope>
       </dependency>
 <!--...-->
</dependencyManagement>
```
- If using Spring, replace `mongock-spring-v5` to `mongock-springboot`

```xml

<dependency>
       <groupId>io.mongock</groupId>
       <artifactId>mongock-springboot</artifactId>
   </dependency>
```

### Mongock packages
| Module               | Version 4                                                            | Version 5 |
|--------------------- | -------------------------------------------------------------------- | -------------------- |
| Spring runner        | com.github.cloudyrock.spring.v5                                      | io.mongock.runner.springboot      |
| Spring runner        | com.github.cloudyrock.spring.util.events                             | io.mongock.runner.spring.base.events |
| Standalone runner    | com.github.cloudyrock.standalone                                     | io.mongock.runner.standalone |
| MongoDB V3 driver    | com.github.cloudyrock.mongock.driver.mongodb.v3                      | io.mongock.driver.mongodb.v3
| MongoDB Sybc4 driver | com.github.cloudyrock.mongock.driver.mongodb.sync.v4                 | io.mongock.driver.mongodb.sync.v4 |
| SpringData V2 driver | com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator | Not changed |
| SpringData V2 driver | com.github.cloudyrock.mongock.driver.mongodb.springdata.v2           | io.mongock.driver.mongodb.springdata.v2 |
| SpringData V3 driver | com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator | Not changed |
| SpringData V2 driver | com.github.cloudyrock.mongock.driver.mongodb.springdata.v3           | io.mongock.driver.mongodb.springdata.v3 |

### Mongock classes
| Module               | Version 4                                                                                 | Version 5 |
|--------------------- | ----------------------------------------------------------------------------------------- | -------------------- |
| Spring runner        | com.github.cloudyrock.spring.v5.MongockSpring5                                            | io.mongock.runner.springboot.MongockSpringboot |
| Spring runner        | com.github.cloudyrock.spring.v5.MongockSpring5.MongockApplicationRunner                   | io.mongock.runner.springboot.base.MongockApplicationRunner |
| Spring runner        | com.github.cloudyrock.spring.v5.MongockSpring5.MongockInitializingBeanRunner              | io.mongock.runner.springboot.base.MongockInitializingBeanRunner |


### Deprecations

#### ChangeLogs/ChangeSets
From version 5, annotations `@ChangeLog` and `@ChangeSet` are deprecated, but it will always remain in code for backwards compatibility.
Once the official documentation for the Version 5 is released, we'll explain how to proceed and why is the motivation for such a change.

#### MongockTemplate
From version 5, class `MongockTemplate`is deprecated, but it will always remain in code for backwards compatibility.
We recommend leaving  old changeLogs  untouched (using with the deprecated MongockTemplate), but use Spring MongoTemplate for new
changeLogs.





