#! /bin/sh
#$1 -> PR number

# It uses token from Github that only has access to comment and update the PR's status
mvn sonar:sonar \
        -Psonarqube \
        -Dmaven.test.failure.ignore=true \
        -Dsonar.analysis.mode=preview \
        -Dsonar.github.pullRequest=$1 \
        -Dsonar.github.repository=cloudyrock/mongock \
        -Dsonar.github.oauth=7416a97904ca7f19ffbebf36338f2a171888d41c \
        -Dsonar.host.url=https://sonarcloud.io \
        -Dsonar.organization=dieppa-github \
        -Dsonar.login=b399a4341c829aa96ec3a1c164ca1db1dc0e8e0d
