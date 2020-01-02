# 3.2.0.BETA
### Breaking changes
*  [Upgraded library spring-data-mongodb to 2.2.3.RELEASE](https://github.com/cloudyrock/mongock/issues/113) in [PR](https://github.com/cloudyrock/mongock/pull/114)


# 3.1.0.BETA
### Breaking changes
* Renamed version to systemVersion, so startVersion and endVersion in the builder become startSystemVersion and endSystemVersion and version in
changeSet annotation becomes systemVersion

### Bugs fixed
* Fixed bug introduced in version 3.0.8.BETA in issue [Mongock reexecutes ChangeSet on every run](https://github.com/cloudyrock/mongock/issues/107) in [PR](https://github.com/cloudyrock/mongock/pull/108)
__________________________________________________
# 3.0.8.BETA
### Feature
* [Support for new com.mongodb.client.MongoClient ](https://github.com/cloudyrock/mongock/issues/98) in [PR](https://github.com/cloudyrock/mongock/pull/99)
* [Track runAlways changesets ](https://github.com/cloudyrock/mongock/issues/100) in [PR](https://github.com/cloudyrock/mongock/pull/101)
* [Display time duration for every changeset ](https://github.com/cloudyrock/mongock/issues/94) in [PR](https://github.com/cloudyrock/mongock/pull/102)
__________________________________________________
# 3.0.7.BETA
### Bugs fixed
* Fixed [@Profile with SpringBootMongock](https://github.com/cloudyrock/mongock/issues/81)
__________________________________________________
# 3.0.5.BETA
### Bugs fixed
* Fixed [Problem with replaceOne with upsert](https://github.com/cloudyrock/mongock/issues/90)
__________________________________________________
# 3.0.3.BETA
### Improvements 
* Improved logs in [PR](https://github.com/cloudyrock/mongock/pull/83)

### Features and breaking changes
* Feature [Version based schema changes](https://github.com/cloudyrock/mongock/issues/82) in [PR](https://github.com/cloudyrock/mongock/pull/83)
__________________________________________________
# 3.0.0.BETA
### Breaking changes
* Removed com.mongodb.DB support due to deprecation in mongoDB driver in [PR](https://github.com/cloudyrock/mongock/pull/74)
* Removed Jongo support as it's based on com.mongodb.DB in [PR](https://github.com/cloudyrock/mongock/pull/73)

### Bugs fixed
* Fixed [issue 72: Spring Boot 2.1.5](https://github.com/cloudyrock/mongock/issues/72) in [PR](https://github.com/cloudyrock/mongock/pull/77)
* Fixed [issue 68: Illegal Reflective Access Warning with Java 9+](https://github.com/cloudyrock/mongock/issues/68) in [PR](https://github.com/cloudyrock/mongock/pull/77)

### Technical note
* Implementation approach changed from using cglib proxies to implementing decorator pattern for database connectors(MongoDatabase, Jongo and MongoTemplate)
__________________________________________________
# 2.0.2
### Bugs fixed
* Fixed [issue 65: Injections in SpringBoot monitored by the lock](https://github.com/cloudyrock/mongock/issues/65) in [PR](https://github.com/cloudyrock/mongock/pull/69)

### Bugs fixed
* Fixed [issue 59: Builder's children classes return parent class in methods where it should return the actual class](https://github.com/cloudyrock/mongock/issues/59) in [PR](https://github.com/cloudyrock/mongock/pull/60)
__________________________________________________
# 2.0.0 
### Features and breaking changes
* Feature [Upgrade to Spring 5, Springboot 2.x and Spring data 2.x](https://github.com/cloudyrock/mongock/issues/20) in [PR](https://github.com/cloudyrock/mongock/pull/45)
* Moved to [JDK 1.8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

### Bugs fixed
* Fixed [issue 53: Use mongock with springboot 2.0.7 but do not work](https://github.com/cloudyrock/mongock/issues/53) in [PR](https://github.com/cloudyrock/mongock/pull/45)
__________________________________________________
# 1.16.2
### Features
* Fixed [issue 55: Exception when using MongoDatabase](https://github.com/cloudyrock/mongock/issues/55) in [PR](https://github.com/cloudyrock/mongock/pull/56)

### Bugs Fixed
* Added changelog.md file
