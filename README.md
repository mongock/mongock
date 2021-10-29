<p align="center" >
    <img src="https://raw.githubusercontent.com/cloudyrock/mongock/master/misc/logo-with-title.png" width="100%" />
</p>
<h3 align="center" style="vertical-align: top;">
MongoDB version control tool for Java
</h4>
<br />
<p align="center" >

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.cloudyrock.mongock/mongock/badge.png)](https://search.maven.org/artifact/com.github.cloudyrock.mongock/mongock)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5f9459fc702a4ee389071f77cec97a38)](https://app.codacy.com/gh/cloudyrock/mongock?utm_source=github.com&utm_medium=referral&utm_content=cloudyrock/mongock&utm_campaign=Badge_Grade)
![Build](https://github.com/cloudyrock/mongock/workflows/Build/badge.svg)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=com.github.cloudyrock.mongock&metric=bugs)](https://sonarcloud.io/component_measures?id=com.github.cloudyrock.mongock&metric=bugs)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=com.github.cloudyrock.mongock&metric=vulnerabilities)](https://sonarcloud.io/component_measures?id=com.github.cloudyrock.mongock&metric=vulnerabilities)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](https://github.com/dieppa/mongock/blob/master/LICENSE)

</p>

<script>
function hello() {
alert("hello!");    
}
</script>

<button onclick="hello()">Click me</button>

**Mongock** is a Java based migration tool as part of your application code. It allows developers to have ownership and control over data migrations during the deployment process, allowing safer migrations in distributed systems as code and data changes are shipped together.  

## Documentation
The official documentation can be found [here][documentation_link]. 

## Examples
[Mongock examples](https://github.com/cloudyrock/mongock-examples)

## Changelog
To see our changelog, please take a look to  our [github releases][github_releases]

## Old versions
With new major releases, like now with version 4, we stop enhancing or adding new features to older version(like version 3).
However, we'll keep providing support and bug fixes for a long period. We are working on an official support plan.

If you are still working with version 3, we suggest you to upgrade to the last version, otherwise you can see the old documentation 
in [here][documentation_v3] 

## Contributing
If you would like to contribute to Mongock project, please read [how to contribute][contributing] for details on our collaboration process and standards.

## Code of conduct
Please read the [code of conduct][codeOfConduct] for details on our code of conduct.

## LICENSE
Mongock propject is licensed under the [Apache License Version 2.0][apacheLicense]. See the [LICENSE][mongockLicense] file for details

[contributing]: ./community/CONTRIBUTING.md
[codeOfConduct]: ./community/CODE_OF_CONDUCT.md
[mongockLicense]: ./LICENSE.md

[apacheLicense]: http://www.apache.org/licenses/LICENSE-2.0.html
[ApplicationRunner]: https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/ApplicationRunner.html
[InitializingBean]: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/beans/factory/InitializingBean.html
[ApplicationContext]: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/ApplicationContext.html
[MongoTemplate]: https://docs.spring.io/spring-data/mongodb/docs/current/api/org/springframework/data/mongodb/core/MongoTemplate.html
[converter]: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/convert/converter/Converter.html
[customConversions]: https://docs.spring.io/spring-data/data-mongodb/docs/current/api/org/springframework/data/mongodb/core/convert/CustomConversions.html
[MongoDatabase]: http://mongodb.github.io/mongo-java-driver/3.6/javadoc/?com/mongodb/client/MongoDatabase.html
[DB]: http://mongodb.github.io/mongo-java-driver/3.6/javadoc/?com/mongodb/DB.html
[JHipster]: https://www.jhipster.tech/
[spring]: https://spring.io/
[springboot]:https://spring.io/projects/spring-boot
[mongoAtlas]: https://www.mongodb.com/cloud/atlas
[evalDocumentation]: https://docs.mongodb.com/manual/reference/method/db.eval/#sharded-data
[sampleProject]: https://github.com/cloudyrock/mongock-integration-tests/tree/master/mongock-4/mongock-spring-v5/mongock-spring5-springdata3-it
[documentation_link]: https://www.mongock.io/
[documentation_v3]: ./community/README_V3.md
[github_releases]: https://github.com/cloudyrock/mongock/releases
