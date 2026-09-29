---
name: java-conventions
description: Applies our team's Java naming, package structure, and layering conventions. Use when writing new Java classes, reviewing where a class should live, or asked to check/fix naming and project structure. Fill in the team's actual agreed conventions below before relying on this.
---

# Java Conventions

NOTE: Replace the placeholders below with what your team actually agreed on.
This skill is only useful once it reflects your real conventions, not generic advice.

## Naming
- Classes: `PascalCase`, singular nouns (`Order`, not `Orders` or `OrderData`).
- Methods and variables: `camelCase`, verbs for methods (`calculateTotal`), nouns
  for variables.
- Constants: `UPPER_SNAKE_CASE`.
- Packages: all lowercase, no underscores (e.g. `com.team.project.<layer>`).

## Package / layer structure
Fill in your team's actual layout, e.g.:
- `controller` – <what goes here, if you're using this layer yet>
- `service` – business logic
- `repository` / `dao` – data access
- `model` / `domain` – plain data classes
- One class per file; file name matches the public class name exactly.

## Formatting
- <Indentation: e.g. 4 spaces, no tabs>
- <Brace style: e.g. opening brace on the same line>
- <Max line length, if agreed>
- <Import ordering, if agreed>

## Rules
- When creating a new class, ask which layer it belongs in if it's not obvious
  from context, rather than guessing.
- Point out when a class breaks an agreed convention, but explain which
  convention and why, since the team is still learning these.
- Don't invent conventions the team hasn't agreed on; ask instead of assuming.
