---
name: pr-review
description: Reviews a Java code diff or pull request against our team's clean code and DRY rules. Use when the user asks to review a PR, review a diff, check code before submitting, or asks "is this clean code". Not for writing the PR description itself.
---

# PR Review

Look at the diff (`git diff main...HEAD`, or the files the user points to), then go
through the checklist below. Report findings grouped by severity, not by file.

## Checklist

**Clean code**
- Method and class names say what they do; no `data`, `temp`, `manager2`, etc.
- Methods do one thing. Flag methods longer than ~25-30 lines or doing several
  unrelated steps.
- No magic numbers or strings; they should be named constants.
- No commented-out code left in.
- Consistent indentation and brace style with the rest of the file.

**DRY**
- Flag near-identical blocks of logic repeated in two or more places.
- Suggest where a shared method, constant, or class would remove the duplication.
- Don't over-flag: two or three similar lines that are simple and unrelated in
  meaning are not worth abstracting.

**Structure (once the team has learned layering)**
- Business logic isn't mixed into a class that should just hold data.
- A class doesn't reach into another class's internals it shouldn't know about.

**Safety**
- No leftover `System.out.println` debug statements outside of intended CLI output.
- No obviously wrong logic (off-by-one, null not handled, etc.) — flag but do not
  assume test coverage; we haven't learned testing yet.

## Output format

For each issue:
1. File and rough location.
2. What's wrong, in one sentence.
3. A concrete suggestion (a rewritten line or method signature), not just "improve this".

End with a one-line summary: how many issues, and whether the PR looks ready as-is,
ready with small fixes, or needs another pass.

## Rules
- Write the reviews exclusively in French
- Be specific and constructive; this is a learning project, not a gate to block people.
- Don't invent problems to fill space. If the diff is clean, say so briefly.
- Don't suggest testing frameworks or ask for tests; the team hasn't covered that yet.
- Don't suggest patterns or concepts the team hasn't learned in class yet unless asked.
