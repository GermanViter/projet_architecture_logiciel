---
name: pr-description
description: Writes pull request descriptions for our team's Java API project. Use when the user asks to create, write, or improve a PR description, or says they are about to open a pull request. Not for code review.
---

# PR Description

Look at the branch changes first (`git diff main...HEAD` and `git log main..HEAD --oneline`), then write the description in this format (headings are in French on purpose, copy them as-is):

## Résumé
One or two sentences: what this PR does and why.

## Changements
- Short bullet per meaningful change (group by class or package)
- Mention new, renamed, or deleted classes

## Comment tester
Steps a teammate can follow to check it works
(for example: run the app, call a method, run the tests).
If a test is not required (e. g. changes in documentation), then don't add this section in the pull request.

## Issue liée
`Closes #<number>` (ask the user for the number if unknown)
If it doesn't close any issue, then don't include this section.

## Notes pour les relecteurs
Anything unsure, any trade-off made, or anything intentionally left out.

## Rules
- Write everything in French, including the section headings: use exactly the French headings above (`Résumé`, `Changements`, `Comment tester`, `Issue liée`, `Notes pour les relecteurs`) and never the English ones (Summary, Changes, How to test, Related issue, Notes for reviewers). Use plain language a teammate can skim in 30 seconds.
- Explain the why, not just the what; the diff already shows the what.
- Keep the PR focused. If the changes mix unrelated things
  (for example a refactor plus a new feature), suggest splitting it.
- Do not invent test steps or issue numbers. Ask if unsure.
- Do not add any line that mentions that this pull request is generated with claude code. (e. g.  Generated with \[Claude Code](https:/claude.com/claude-code)).
- All pull requests need to be merged into the develop branch unless specified otherwise.