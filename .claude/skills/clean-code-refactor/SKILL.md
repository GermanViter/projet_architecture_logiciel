---
name: clean-code-refactor
description: Finds duplicated code and long/unclear methods in our Java project and proposes refactors, explaining each one. Use when the user asks to clean up, refactor, simplify, or "DRY up" a file or class, or asks for feedback on code quality outside of a PR review.
---

# Clean Code Refactor

Read the file(s) the user points to (or ask which file if none is given). For each
issue found, propose a fix — don't just list problems.

## What to look for
- **Duplication**: same or near-same logic in two or more places. Propose a shared
  private method, a helper class, or a constant, whichever fits.
- **Long methods**: a method doing several distinct steps. Propose splitting it
  into smaller, named private methods, one per step.
- **Unclear names**: variables/methods/classes named after their type or position
  (`list1`, `helper`, `doStuff`) rather than their purpose.
- **Magic values**: hardcoded numbers or strings that mean something specific.
  Propose a named constant.
- **Deep nesting**: several levels of if/for nested together. Propose early
  returns or extracted methods to flatten it.

## Output format
For each issue:
1. Quote the specific lines (or method name) affected.
2. Explain in one sentence why it's worth changing.
3. Show the refactored version.

Then show the full refactored file (or ask before rewriting the whole file, if it's long).

## Rules
- Preserve behavior exactly. Never change what the code does while cleaning it up.
- Don't introduce design patterns, frameworks, or testing code the team hasn't
  covered in class yet — keep refactors at the level of plain clean code and DRY.
- If the file is already reasonably clean, say so rather than inventing changes.
- Explain each refactor like you're teaching it, since this is for a class.
