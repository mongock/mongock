# 3.0.0
### Breaking changes
* Removed com.mongodb.DB support due to deprecation in mongoDB driver in [PR](https://github.com/cloudyrock/mongock/pull/72)

### Bugs fixed
* Fixed [issue 72: Spring Boot 2.1.5](https://github.com/cloudyrock/mongock/issues/72) in [PR](https://github.com/cloudyrock/mongock/pull/73)
* Fixed [issue 68: Illegal Reflective Access Warning with Java 9+](https://github.com/cloudyrock/mongock/issues/68) in [PR](https://github.com/cloudyrock/mongock/pull/73)

### Technical note
* Implementation approach changed from using cglib proxies to implementing decorator pattern for database connectors(MongoDatabase, Jongo and MongoTemplate)
__________________________________________________
# 2.0.2
### Bugs fixed
* Fixed [issue 65: Injections in SpringBoot monitored by the lock](https://github.com/cloudyrock/mongock/issues/65) in [PR](https://github.com/cloudyrock/mongock/pull/69)


### Bugs fixed
* Fixed [issue 59: Builder's children classes return parent class in methods where it should return the actual class](https://github.com/cloudyrock/mongock/issues/59) in [PR](https://github.com/cloudyrock/mongock/pull/60)
__________________________________________________
# 2.0.0 (14/12/2018)
### Features and breaking changes
* Feature [Upgrade to Spring 5, Springboot 2.x and Spring data 2.x](https://github.com/cloudyrock/mongock/issues/20) in [PR](https://github.com/cloudyrock/mongock/pull/45)
* Moved to [JDK 1.8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
### Bugs fixed
* Fixed [issue 53: Use mongock with springboot 2.0.7 but do not work](https://github.com/cloudyrock/mongock/issues/53) in [PR](https://github.com/cloudyrock/mongock/pull/45)
__________________________________________________
# 1.16.2 (13/12/2018)
### Features
* Fixed [issue 55: Exception when using MongoDatabase](https://github.com/cloudyrock/mongock/issues/55) in [PR](https://github.com/cloudyrock/mongock/pull/56)

### Bugs Fixed
* Added changelog.md file
