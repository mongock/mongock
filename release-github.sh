#!/usr/bin/env bash
export GPG_TTY=$(tty)
mvn -B release:prepare release:perform -Dmaven.javadoc.skip=true
