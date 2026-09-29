---
name: write-issue
description: Turns a rough idea, bug, or task into a well-formed GitHub issue for our Kanban board. Use when the user describes something to build or fix and wants it written up as an issue, or asks to "open an issue" or "add this to the board".
---

# Write Issue

Turn what the user describes into a clear issue using the format below. Ask a short
clarifying question only if the request is too vague to write a real acceptance
criterion (for example, missing which endpoint or class it concerns).

## Format

**Title**
Short, action-oriented (e.g. "Add validation for empty product name").

**Type**
One of: `feature`, `bug`, `chore`, `refactor`.

**Description**
1-3 sentences: what needs to happen and why, in plain language.

**Acceptance criteria**
- A short checklist of concrete, checkable conditions ("Given/When/Then" is fine,
  but plain bullets are okay too).
- 2-5 items; avoid vague criteria like "works correctly".

**Size**
A rough estimate: `S`, `M`, or `L`, based on how many classes/files it likely touches.
State the assumption behind the estimate in one clause.

**Notes**
Anything relevant: related classes, a related issue number, or a design decision
that needs to be made first.

## Rules
- Write the issues exclusively in French
- Keep it to something one person could realistically pick up and understand
  without asking the reporter questions.
- Don't invent acceptance criteria that assume features the team hasn't built yet.
- If the user's idea is really two separate issues, say so and offer to split it.
- Match the labels/columns to the team's board if the user has described one;
  otherwise just use the `type` field above.
- Never mention that this issue is authored by claude code.
