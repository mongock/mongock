 # Contributing to Mongock project

 :+1::tada: First off, thanks for taking the time to contribute! :tada::+1:

 The following is a set of guidelines for contributing to Mongock project which is hosted on GitHub. These are mostly guidelines, not rules. Use your best judgment, and feel
 free to propose changes to this document in a pull request.

## Table of contents

* [Code of Conduct](#code-of-conduct)
* [It's just a question, I don't want to read this whole thing!!!](#its-just-a-question-i-dont-want-to-read-this-whole-thing)
* [Mongock project packages](#mongock-project-packages)
* [Reporting Bugs](#reporting-bugs)
   * [Before Submitting A Bug Report](#before-submitting-a-bug-report)
   * [How Do I Submit A (Good) Bug Report?](#how-do-i-submit-a-good-bug-report)
* [Suggesting Enhancements](#suggesting-enhancements)
   * [Before Submitting An Enhancement Suggestion](#before-submitting-an-enhancement-suggestion)
   * [How Do I Submit A (Good) Enhancement Suggestion?](#how-do-i-submit-a-good-enhancement-suggestion)
* [Code Contribution](#code-contribution)
   * [I just want to help: First code contribution](#i-just-want-to-help-first-code-contribution)
   * [Code contribution steps](#code-contribution-steps)
* [Issue and Pull Request Labels](#issue-and-pull-request-labels)
   * [Type of issue](#type-of-issue)
   * [Module](#module)
   * [Severity](#severity)
   * [Complexity](#complexity)


## Code of Conduct

This project and everyone participating in it is governed by the [Mongock Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code. 
Please report unacceptable behavior to [dev@cloudyrock.io][dev_email].

## It's just a question, I don't want to read this whole thing!!!

> **Note:** Please don't file an issue to ask a question.
 
We have an official [FAQ page](FAQ.md) where your question may be already answered. Otherwise an email to [dev@cloudyrock][dev_email]  will be the fastest way to get your question resolved. 
 
## Mongock project packages
Mongock project contains 2 main areas. 
- **mongock-core:** Basic Mongock tool for standalone projects, although can be used perfectly with frameworks like Spring. Issue label is `core-module`
- **mongock-spring:** Mongock module which takes advantage of Spring features. Issue label is `spring-module`

## Reporting Bugs

This section guides you through submitting a bug report for Mongock project. 

> **Note:** If you find a **Closed** issue that seems like it is the same thing that you're experiencing, open a new issue and include a link to the original issue in the body of your new one.

### Before Submitting A Bug Report

* **Check the [FAQ page](FAQ.md)** for a list of common questions and problems. You might find out your issue/question is clarified in this page.
* **Perform a [cursory search](https://github.com/issues?utf8=%E2%9C%93&q=is%3Aissue+archived%3Afalse+repo%3Acloudyrock%2Fmongock)** 
to see if the problem has already been reported. If it has **and the issue is still open**, add a comment to the existing issue instead of opening a new one.

### How Do I Submit A (Good) Bug Report?

Bugs are tracked as [GitHub issues](https://guides.github.com/features/issues/). Create an issue on the Mongock repository following the
[issue template](ISSUE_TEMPLATE.md). Please provide as much information as you can and add the following labels to make easier to categorise issues: 
- The label `bug`
- The module or modules involved: [module labels](#module)
- Severity of the bug: [severity labels](#severity)
- If you feel confident to estimate how complex the fix is, please provide a complexity label: [complexity labels](#complexity)

## Suggesting Enhancements

This section guides you through submitting an enhancement suggestion for Mongock project, including completely new features and minor improvements to existing functionality.

### Before Submitting An Enhancement Suggestion


* **Check the [FAQ page](FAQ.md)**. You might find out that enhancement is already covered.
* **Determine [which module the enhancement should be reported in](#mongock-project-packages)**.
* **Perform a [cursory search](https://github.com/issues?utf8=%E2%9C%93&q=is%3Aissue+archived%3Afalse+repo%3Acloudyrock%2Fmongock)** to see if the problem has already been reported. If it has **and the issue is still open**, add a comment to the existing issue instead of opening a new one.


### How Do I Submit A (Good) Enhancement Suggestion?

Enhancement suggestions are tracked as [GitHub issues](https://guides.github.com/features/issues/). Create an enhancement on the main repository and provide the required information
by filling in [the enhancement template](ENHANCENMENT_TEMPLATE.md).
 
Please add the following labels to make easier to categorise issues:
- The label `feature`
- The module or modules involved: [module labels](#module)
- Severity of the bug: [severity labels](#severity)
- If you feel confident to estimate how complex the development is, please provide a complexity label: [complexity labels](#complexity

## Code Contribution

### Synchronizing efforts
> In order to let us known(and the rest of commiters) you are working on an issue, please add a comment in the issue itself. You don't need to provide 
any other information than a single "I'm working on this", as the issue will be used as communication channel.
Obviously, If the issue you are about to work on already contains such a comment, you better don't work on this issue as there is someone else working already on it. 

### I just want to help: First code contribution

Unsure where to begin contributing to Mongock project? You can start by looking through these `beginner` and `intermediate` issues:

* [Beginner issues](https://github.com/cloudyrock/mongock/issues?q=is%3Aopen+is%3Aissue+label%3Abeginner) - issues which should only require a few lines of code, and a test or two.
* [Intermediate issues](https://github.com/cloudyrock/mongock/issues?q=is%3Aopen+is%3Aissue+label%3Aintermediate) - issues which should be a bit more involved than `beginner` issues.

### Code contribution steps
1. __Issue created__: Before starting a code contribution, please make sure there is an issue for it.
1. __Fork repository__: Fork this project to your own repository.
1. __Branch within forked repository__: In your forked repository create a branch, in which you will do the change, using the pattern 'feature/issue_xx'.
1. __Coding__: Make sure your code change fits [our standards](./CODE_STANDARDS.md).
1. __Local verification__: This stage is intended to verify the code fits some basic thresholds. See [our verification process](./VERIFICATION_PROCESS.md)
1. __README update__: Update README.md file when required.
1. __Pull request__: Use our [pull request template](PULL_REQUEST_TEMPLATE.md) to provide the required information.

Once a pull request in in place, you can expect 1-2 days for us to start the review.

Part of the review involves sonar to check the code quality. Although it's already explained in [our standards](./CODE_STANDARDS.md), please notice:
* We use a [quality profile][sonar-quality-profily-url], which is basically an small extension of the sonar way with few changes.
* Our [quality gate][sonar-quality-gate-url] requires at least 85% test coverage and at most 3% code duplication for new code.


## Issue and Pull Request Labels

This section lists the labels we use to help us track and manage issues and pull requests. 

The labels are loosely grouped by their purpose, but it's not required that every issue have a label from every group or that an issue can't have more than one label from the same group. However,
the more you provide, the easier and faster the issue can be fixed.

Please click on the `search` list the issues for the given label.


### Type of issue

| Label name | `Search in github` :mag_right: | Description |
| --- | --- | --- |
| `bug` | [search][search-repo-label-bug] | Bug issues |
| `feature` | [search][search-repo-label-feature] | Feature issues |
| `docs/ops` | [search][search-repo-label-docs-ops] | Issues which just require documentation or some management work |


### Module

| Label name | `Search in github` :mag_right: | Description |
| --- | --- | --- |
| `core-module` | [search][search-repo-label-core-module] | Issues to be fixed in the core |
| `spring-module` | [search][search-repo-label-spring-module] | Issues to be fixed in the server module |

### Severity

| Label name | `Search in github` :mag_right: | Description |
| --- | --- | --- |
| `critical` | [search][search-repo-label-critical] | Blocking issues |
| `high` | [search][search-repo-label-high] | Very important, but non-blocking, issues |
| `normal` | [search][search-repo-label-normal] | Normal non-blocking issues that needs to be fixed sooner than later  |
| `minor` | [search][search-repo-label-minor] | Low priority issues |

### Complexity

| Label name | `Search in github` :mag_right: | Description |
| --- | --- | --- |
| `beginner` | [search][search-repo-label-beginner] | Issues which should only require a few lines of code, and a test or two |
| `intermediate` | [search][search-repo-label-intermediate] | Issues which should be a bit more involved than `beginner` issues  |
| `advanced` | [search][search-repo-label-advanced] | Issues that require some deep knowledge, expertise or just time consuming |

### Others

| Label name | `Search in github` :mag_right: | Description |
| --- | --- | --- |
| `invalid/won't fix` | [search][search-repo-label-wont-fix] | issues which won't be worked on|



[dev_email]: mailto:dev@cloudyrock.io
[sonar-quality-profily-url]:https://sonarcloud.io/organizations/cloudyrock/rules?activation=true&qprofile=AWXxxqaLFozSdSzRWmfS
[sonar-quality-gate-url]:https://sonarcloud.io/organizations/cloudyrock/quality_gates/show/1437

[search-repo-label-bug]:https://github.com/cloudyrock/mongock/labels/bug
[search-repo-label-feature]:https://github.com/cloudyrock/mongock/labels/feature
[search-repo-label-docs-ops]:https://github.com/cloudyrock/mongock/labels/docs%2Fops

[search-repo-label-core-module]:https://github.com/cloudyrock/mongock/labels/core-module
[search-repo-label-spring-module]:https://github.com/cloudyrock/mongock/labels/spring-module

[search-repo-label-critical]:https://github.com/cloudyrock/mongock/labels/critical
[search-repo-label-hig]:https://github.com/cloudyrock/mongock/labels/high
[search-repo-label-normal]:https://github.com/cloudyrock/mongock/labels/normal
[search-repo-label-minor]:https://github.com/cloudyrock/mongock/labels/minor

[search-repo-label-beginner]:https://github.com/cloudyrock/mongock/labels/beginner
[search-repo-label-intermediate]:https://github.com/cloudyrock/mongock/labels/intermediate
[search-repo-label-advanced]:https://github.com/cloudyrock/mongock/labels/advance

[search-repo-label-wont-fix]:https://github.com/cloudyrock/mongock/labels/invalid%2Fwon%27t%20fix
