#! /bin/sh
#$1 -> PR number

# It uses token from Github that only has access to comment and update the PR's status
mvn sonar:sonar \
  -Psonarqube \
  -Dmaven.test.failure.ignore=true \
  -Dsonar.analysis.mode=preview \
  -Dsonar.organization=cloudyrock \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.login=8a2c9277b6f448baf591145b6be9503c78448aa6 \
  -Dsonar.github.repository=cloudyrock/mongock \
  -Dsonar.github.oauth=be8d5a8b528d60bb74517e8fb6e6e6657ab95ba7 \
  -Dsonar.github.pullRequest=$1