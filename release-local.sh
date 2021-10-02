#!/usr/bin/env bash
export GPG_TTY=$(tty)
git add .
git commit -m "$@"
git push origin develop
git checkout master
git rebase develop
git push origin master
git checkout develop