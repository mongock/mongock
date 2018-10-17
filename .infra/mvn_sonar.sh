#!/bin/bash

printf "____________________________________________________________________Branch:\t\t\t $TRAVIS_BRANCH\n"
printf "____________________________________________________________________Pull request key:\t\t $TRAVIS_PULL_REQUEST\n"
printf "____________________________________________________________________Pull request branch:\t $TRAVIS_PULL_REQUEST_BRANCH\n"
printf "____________________________________________________________________Encrypted variables:\t $TRAVIS_SECURE_ENV_VARS\n"
printf "____________________________________________________________________Encrypted variables:\t $TRAVIS_REPO_SLUG\n"

if [[ -z $TRAVIS_PULL_REQUEST_BRANCH ]]
then
    echo "Verifying and running sonar for  master branch"
    mvn clean verify sonar:sonar -Dsonar.projectKey=com.github.cloudyrock.mongock -Dsonar.organization=cloudyrock -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=$SONAR_TOKEN
elif [[ $TRAVIS_SECURE_ENV_VARS = true ]]
then
    echo "Verifying and running sonar for internal pull request from branch $TRAVIS_PULL_REQUEST_BRANCH"
    mvn clean verify sonar:sonar -Dsonar.projectKey=com.github.cloudyrock.mongock -Dsonar.organization=cloudyrock -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=$SONAR_TOKEN -Dsonar.pullrequest.base=master -Dsonar.pullrequest.key=$TRAVIS_PULL_REQUEST -Dsonar.pullrequest.branch=$TRAVIS_PULL_REQUEST_BRANCH
else
    echo "Verifying(NO SONAR) external pull request from forked repository $TRAVIS_REPO_SLUG, branch $TRAVIS_PULL_REQUEST_BRANCH"
    mvn clean verify
fi