
# Sample project for Mongock V4.x.x with Spring 5 and Spring-data 3

## Notes
* Spring 5 version must be grater(or equal) than 2.3.0.M2
* It uses an alpha version of Mongock
* Official documentation for Mongock V4.0.X has not been released yet
* It uses a RC for Spring-data, so spring milestone repository is required for maven to retrieve the dependencies
* It requires a MongoDB instance running
* Currently does not take into account changeLogs from previous versions. 
This means it will probably re-run changeLogs from older migrations. 
This will be fixed soon in next alpha releases.
* Builder API has changed considerably. Please read brief explanation below.
* In case of using spring-data, please notice you must use Mongo**ck**Template, instead of MongoTemplate.
Both provide the same API, so you don't miss anything, but Mongo**ck**Template is a sort of decorator providing 
lock support. Actually, you would get an error if using MongoTemplate in your changeLogs.
* If you are using spring-data, currently, if you use your own spring repositories in your changeLogs,
they won't be covered by the lock. **This will be provided in next releases of this version, though**
* You will see a warn from spring-data like `Automatic index creation will be disabled by default as of Spring Data MongoDB 3.x`.
For simplicity, we haven't set up the indexes manually(probably we'll need to do it when upgrading this project), but you should probably do.
* There are two ChangeSet and @ChangeLog annotation pairs. One pair implementing in Mongock project and another pair implemented in Changock project
(this is the core project in which Mongock is based on). Currently there is a bug and Changock annotations are not processed, only Mongock ones. This will bbe
also fixed in next releases

## New Builder approach
Before version 4, Mongock was tightly coupled to MongoDB driver and connection libraries, such as spring-data.
The issues with this approach is that in order to keep evolving Mongock with new features and supporting new versions
of these libraries, it forces users to upgrade to these libraries, when could not be desired or even possible.
So a mechanism for supporting older drivers and connection libraries, while keep evolving Mongock is a must.

For this reason we have started a new approach for building Mongock. Now you need two things: a runner and a driver.
* Driver: It's the connection library or MongoDB java driver. It's responsible of everything related to the persistence 
of the changeLogs, as well as  providing the relevant tools to the changeSets to access to database
(for example, in case of spring data, providing Mongo**ck**Template, etc.)
* Runner: It's wrapper of the driver, which is responsible of taking the changeLogs classes, process them and use
the driver to interact with the database. This is very related with the framework used. Currently there are two options, 
standalone and spring-5(which also provides two other options ApplicationRunner and InitializingBean). But other options 
could be added in the future in order to support other frameworks, such as Micronaut, etc.





