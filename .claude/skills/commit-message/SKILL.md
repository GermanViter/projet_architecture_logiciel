---
name: commit-message
description: Writes a commit message for the currently staged changes in our team's format. Use when the user asks to write, suggest, or check a commit message, or says they're about to commit.
---

# Commit Message

Look at what's staged (`git diff --cached`; if nothing is staged, use
`git diff` and say you're describing unstaged changes) and write a message in
this format:

```
<type>: <short summary, imperative mood, under 60 chars>

<optional body: what changed and why, only if the summary isn't enough>

<optional: Refs #<issue-number>>
```

## Types
- `feat` – new functionality
- `fix` – bug fix
- `refactor` – code change with no behavior change
- `chore` – config, formatting, dependencies, non-code files
- `docs` – documentation only

## Rules
- One logical change per commit. If the staged diff clearly mixes unrelated
  changes (e.g. a new feature plus reformatting an unrelated file), say so and
  suggest splitting into separate commits instead of writing one message for both.
- Imperative mood: "add validation", not "added validation" or "adds validation".
- No period at the end of the summary line.
- Don't reference an issue number unless the user gives one or it's clearly in
  the branch name (e.g. `feature/12-add-login`).
- Keep the body out unless the summary line genuinely needs explaining — most
  commits in a small student project don't need one.
- Write every commit exclusively in French as this is project is for a class that is tought in French.
