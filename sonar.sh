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
  -Dsonar.github.oauth=27364b8acc93f7e708e8b4582df3e745db2163b2 \
  -Dsonar.github.pullRequest=$1

