#!/usr/bin/env bash

export GPG_TTY=$(tty)
mvn release:perform -Dmaven.javadoc.skip=true