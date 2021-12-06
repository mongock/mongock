 # Contributing to Mongock project

 :+1::tada: First off, thanks for taking the time to contribute! :tada::+1:

 The following is a set of guidelines for contributing to Mongock project which is hosted on GitHub. These are mostly guidelines, not rules. Use your best judgment, and feel
 free to propose changes to this document in a pull request.


## Getting started

<p style="background-color: #e7f6f0;border: solid 1px #27ae60;;padding: 15px 60px;">
If you want to contribute making a pr, visit our [contribution requested page](https://github.com/mongock/mongock/labels/contribution-requested) and you will find good candidates
</p>
 
Contributions are made to this repo via **issues**, **discussions** and **pull request**.

Within the Pull Request section, there is a special kind of contribution that deserves special attention:  [Creation of new drivers](#providing-a-new-driver)


### Issues
For raising a new issue, please visit this [link](https://github.com/mongock/mongock/issues/new?assignees=&labels=&template=bug_report.md&title=), fills the template and submit it. 
You will hear from us shortly.

### Discussions
For general **Q&A**s, **Ideas**, **comment on any release** or anything you want to raise, but it's not an issue, please visit the [discussion page](https://github.com/mongock/mongock/discussions)

### Pull requests

To perform a pull request, please follow these steps:
1. Create an issue spotting the bug or change your PR will address
2. Fork the `develop` branch
3. Perform the pertinent change
4. Add the required unit/integration tests
5. Update the documentation accordingly in [this project](https://github.com/mongock/mongock-docs)
6. Raise a pull request for the documentation   
7. Raise a pull request for the actual change in the Mongock project, filling up the form you will find and providing the documentation pull request.
8. We'll review both of them shortly and will let you know if any further change or explanation is required or we have everything we need to merge into `develop` branch


Contributors making pull requests, will be listed in our [contributor page](https://www.mongock.io/v5/contribution/contributors)

### Providing a new driver

Submitting a new driver is considered the highest level of external contribution. On top of being in our [contributor page](https://www.mongock.io/v5/contribution/contributors), this kind o contribution are often compensated with bounties.

If you want to contribute by providing a driver that is not in our [bounty-list](https://github.com/mongock/mongock/labels/Bounty%20%3Amoney_mouth_face%3A%3Amoneybag%3A), feel free to contact us [here](mailto:support@mongock.io) to check if that specific driver is subject to any bounty.

To submit a new driver, please follow these steps:

1. Clone this [template repository](https://github.com/mongock/mongock/tree/develop/driver-template) in a independent folder.
2. Follow the instructions in the README.md inside the repo.
3. Once you are happy with your changes, raise a pull request as explained in the [pull requests section](#pull-requests)