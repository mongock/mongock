#! /bin/sh
#$1 -> PR number

# It uses token from Github that only has access to comment and update the PR's status
mvn sonar:sonar \
        -Psonarqube \
        -Dmaven.test.failure.ignore=true \
        -Dsonar.analysis.mode=preview \
        -Dsonar.sources=src/main \
        -Dsonar.tests=src/test \
        -Dsonar.github.pullRequest=5 \
        -Dsonar.github.repository=cloudyrock/mongock \
        -Dsonar.github.oauth=9c7dd30e840e1f24f50a2cf283f63e954d6b7f4f \
        -Dsonar.host.url=https://sonarcloud.io \
        -Dsonar.organization=dieppa-github \
        -Dsonar.login=b399a4341c829aa96ec3a1c164ca1db1dc0e8e0d