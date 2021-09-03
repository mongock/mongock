# Version 5 upgrade steps

### Pom changes
- groupId has change from `com.github.cloudyrock.mongock` to `io.mongock`
- BOM version to latest version: [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.mongock/mongock/badge.png)](https://search.maven.org/artifact/io.mongock/mongock)
- If using Spring, replace `mongock-spring-v5` to `mongock-springboot`


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
<!--...-->
<dependencies>
   <dependency>
       <groupId>io.mongock</groupId>
       <artifactId>mongock-springboot</artifactId>
   </dependency>
   <dependency>
       <groupId>io.mongock</groupId>
       <artifactId>mongodb-springdata-v3-driver</artifactId>
   </dependency>
<!--...-->
</dependencies>
<!--...-->
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


### ChangeLogs/ChangeSets
From version 5, annotations `@ChangeLog` and `@ChangeSet` are deprecated and shouldn't be used (remains in code for backwards compatibility).

Please follow one of the recommended approaches depending on your use case:
 - For existing changeLogs/changeSets created prior version 5: leave them untouched (use with the deprecated annotation)
 - For new changeLogs/changeSets created  from version 5: ChangeLogs/changeSets implement your changelogs by  implementing the interfaces ChangeLog or BasicChangeLog





