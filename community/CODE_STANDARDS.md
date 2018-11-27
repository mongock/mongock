
# Mongock Code Standard

## Table of contents

* [Introduction](#introduction)
* [General](#general)
   * [Column limit: 120](#column-limit-120)
   * [Horizontal alignment: never required](#horizontal-alignment-never-required)
   * [Access modifier](#access-modifier)
 * [Programming Practices](#programming-practices)
   * [final modifier](#final-modifier)
   * [Immutability pattern](#immutability-pattern)
   * [Java 8 Optional Vs null](#java-8-optional-vs-null)
   * [Generic](#generic)
   * [Code duplication](#code-duplication)
   * [Java 8 API](#java-8-api)
* [Test](#test)
   * [Test file Naming](#test-file-naming)
   * [Method naming](#method-naming)
   * [Coverage](#coverage)
* [Javadoc](#javadoc)


## Introduction
--------------------------------------------------------------------------------------------------------------------------------

We use [Google Java Style Guide][google-style-url] as base guide, adding few rules.

Notice that as part of the code review sonarqube is used to check  the code quality, which is configured with:
* [Quality profile](https://sonarcloud.io/organizations/cloudyrock/quality_profiles/show?language=java&name=Mongock+way)
* [Quality gate](https://sonarcloud.io/organizations/cloudyrock/quality_gates/show/1757)


## General
--------------------------------------------------------------------------------------------------------------------------------

### Column limit: 120

We add extra 20 characters to the goggle standard column limit: 120 characters column limit.

This is ok for most of screens and allow developers to split the screen in two, actual class and test.

### Horizontal alignment: never required

> **Terminology Note:** _Horizontal alignment_ is the practice of adding a variable number of additional spaces in your code with the goal of making certain tokens appear directly below certain other tokens on previous lines.

This practice is NOT permitted by Mongock Style.

Here is an example without alignment, then using alignment:

```java
private  int x;  // this is OK
private  Color color;  // this OK too

private  int   x;  // NOT allowed
private  Color color;
```


### Access modifier
In this section we want to encourage the right modifier in every case.

* __package-private__: The default modifier for classes and methods.
* __private__: Default modifier for attributes and for methods accessed only but the current class.
* __protected__: For members which are(or could be) accessed by children classes in a different package.
* __public__: Only for classes, methods and static constants that are(or could be) accessed publicly.


## Programming Practices
--------------------------------------------------------------------------------------------------------------------------------------------------

### final modifier

You should be using `final` modifier for classes and members are instructed in the Java standard guide. In  this section we add specific scenarios where
you should be using `final` too.

* __Utility classes__: In utility classes that are accessed statically.
* __Local variables__: As general rule, use final in variables where reference is not intended to be modified. This helps readability.
* __Method parameter__: Not required, but permitted, applying the rule for 'Local variables'

### Immutability pattern

As part of this project we highly encourage immutability, so please apply it wherever is possible.

### Java 8 Optional Vs null

As general rule avoid the presence of null references as much as possible. As returning type, use `Optional` as container type in any method where the returned object might be absent.

### Generic

As part of this project we like to use meaningful type parameter names, although is opposite to the java standard. So this is not required, but appreciated.

Example:

```java
public interface GenericInterface<GENERIC_PARAMETER> {
...
}
```

### Code duplication

A maximum of 3% code duplication is allowed for new code.

### Java 8 API

Use the Java 8 API where possible.


## Test
--------------------------------------------------------------------------------------------------------------------------------------------------

### Test file Naming

* __Unit tests__: Unit test files must be named using the pattern *UTest.java
* __Integration tests__: Integration test files must be named using the pattern *ITest.java

### Method naming

Use the **should**XXX_**when**XXX_**if**XXXXX pattern for test names.

Example:
```java
public shouldReturn4_whenSum_ifParametersAre2And2() {
...
}

```

### Coverage

Any code delivered must be covered by unit and integrations test. We ensure this by using [Jacoco plugin](https://www.eclemma.org/jacoco/).

Coverage for unit and integrations tests are checked separately(both have __80% threshold coverage__)

You can run the check simply by running `mvn clean verify`

> **Note:** Please notice that when we you run jacoco(by mvn), it checks the entire project. However, the first thing we do when we perform a code review
is to run sonar which checks that just the new code fits the coverage threshold. This means that when you verify locally you coverage, it passes, but it
doesn't when we review. For this we suggest you make sure your new code fits the coverage threshold.


## Javadoc
----------------------------------------------------------------------------------------------------------------------

As part of this project we encourage self-explanatory code, so Javadoc is only required for public members which will be (or could be) used by the users of this library.

Where Javadoc required, we apply the [Google style](http://google.github.io/styleguide/javaguide.html#s7-javadoc)





[google-style-url]:http://google.github.io/styleguide/javaguide.html