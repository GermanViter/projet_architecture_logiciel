# RPG API — Project Context

Turn-based fantasy RPG engine exposed as a REST API (Cégep Marie-Victorin,
420-310-MV, Epic client: Charles Viau). Java. In-memory storage only — no
database, no persistence across restarts. Currently at the "domain first"
stage: no API/HTTP layer, no persistence layer, no tests yet — see
[Current scope](#current-scope-reminders).

## The single goal of every design decision

Everything below — Clean Code, SOLID, DDD — is in service of one thing:
**contain the propagation of change.** A small rule change should touch one
place, not cascade through the system. Before proposing a design, ask:

- Will this change stay local, or will it spread?
- Strong cohesion inside (what changes for the same reason stays together).
- Weak coupling outside (objects collaborate without depending on each
  other's internals).

## Core architectural principle: actions, not state mutation

**The server is the single source of truth.** Game rules live in the engine,
never in a client, and never leak out through a setter either.

- Right: `character.takeDamage(50)` — "Character, take 50 damage." The
  object decides how to respond and which rules apply.
- Wrong: `character.setHealth(character.getHealth() - 50)` — the outside
  world reaches in, does the calculation, and imposes a new value.

This is **Tell, Don't Ask**: ask an object to act, don't ask for its state to
compute the change yourself. It's also the **Law of Demeter**: talk to your
immediate collaborator, not to your collaborator's collaborators
(`character.takeDamage(50)`, never
`character.getHealth().takeDamage(50)`, and never chains like
`character.getInventory().getEquippedWeapon().getDamage().getValue()`).

Apply this everywhere: healing, spells, items, equipment, turns, combat
progression. A client never sets HP, mana, inventory contents, or turn order
directly — always through a domain action.

## Encapsulation and object design

- **Private state, no automatic setters.** A setter that just reassigns a
  field isn't really private — `character.setHealth(-500)` should be
  impossible, not merely discouraged by convention. If a "setter" needs
  conditions to stay valid, it isn't a setter anymore — it's a domain
  behavior; name it after what actually happens (`takeDamage`, `heal`), not
  after the field it touches.
- **Constructors build valid objects, not incomplete ones to patch later.**
  Reject invalid state at construction (empty name, negative health) with a
  clear exception rather than allowing it and validating later.
- **Objects control their own modification.** A concept with its own rules
  deserves its own object (see Primitive Obsession below), and that object —
  not its caller — enforces those rules.
- **Don't expose internal structure with getters just because you can.**
  Before adding a getter: is this really needed outside the object? Before
  adding a setter: does this represent a real domain behavior, or just let
  external code reach in? (Answer for setters is almost always: it's not a
  real behavior — don't add it.)

## Primitive Obsession — give domain concepts their own type

A `String name` or `int health` looks harmless, but if that value has its
own rules (a name can't be blank; health can't go negative or above max), a
primitive can't protect those rules. Wrap it: `CharacterName`, `Health`,
etc. — small classes that own their own validation and let the containing
class delegate to them (`Character.takeDamage()` calls
`health.takeDamage()`) rather than reimplementing the rule.

## Prefer composition over inheritance

Inheritance ties a subclass tightly to its parent — changes in the parent
ripple into every child, and it models "what an object **is**", which
breaks down once behaviors need to be mixed and matched (a character that
can use different weapons, spells, or attack styles). Composition models
"what an object **can do**":

- Define behavior as an interface (a contract): `AttackBehavior { attack() }`.
- Implement each variant separately: `SwordAttack`, `BowAttack`,
  `MagicAttack`.
- The object holds a reference to the behavior and delegates to it, rather
  than knowing every variant itself.

This is the **Strategy pattern** — `Character` (Context) doesn't know *how*
it attacks, only that it holds something that respects the `AttackBehavior`
contract (Strategy). Adding `SpearAttack` later means adding a class, not
touching `Character`. Prefer this over `if`/`switch` on a type/enum wherever
a behavior varies by kind of thing (attack style, spell effect, creature
behavior) — a repeated switch on type is a code smell (see below) meaning an
interface + polymorphism was probably called for instead.

**LSP caution**: if a subtype (or a Strategy implementation) has to refuse
part of the contract it inherits/implements (throwing
`UnsupportedOperationException` for a method that should work), the
inheritance/interface relationship is probably wrong — don't force it.

## Clean Code rules for this project

- **Names reveal intent.** `takeDamage()` communicates far more than
  `setHealth()`. Use the domain's own vocabulary (ubiquitous language, see
  below) in class, method, and variable names.
- **One function, one intention.** If describing what a method does
  requires the word "and", it probably has more than one responsibility —
  split it.
- **Zero comments — the code is the documentation.** A comment explaining
  *why* a rule exists is usually a sign the rule needs a name in code
  instead. `damage *= 2; // critical hit bonus` should become
  `damage = applyCriticalHitBonus(damage);`.
- **Errors are part of the contract, and they should speak the domain.**
  Prefer a domain exception (`InvalidCharacterNameException`) over a generic
  one (`IllegalArgumentException`) — it lets calling code react to the
  specific rule that was violated, and the exception's type documents which
  rule that was.
- **Parameter Objects for data that always travels together.** If several
  values are always passed together to represent one message
  (`attacker, target, weapon, bonus, critical`), wrap them in a record
  (`AttackArgs`) — it keeps signatures stable as the message grows and makes
  the contract more readable.

### Code smells to watch for (and what they usually mean)

| Smell | Usually means |
|---|---|
| Duplication | DRY violation — same rule exists in >1 place |
| Long Method | SRP violated at method scope — split it |
| Feature Envy | A method cares more about another object's data than its own — move the behavior there, or call `object.doThing()` instead of reading its state to do it yourself |
| God Class | Too many responsibilities in one class — the main risk in this project; watch it especially for `Character`/`Hero` |
| Primitive Obsession | A domain concept hiding behind a `String`/`int` — give it a type |
| Data Clumps | Same group of values always traveling together — extract a Parameter Object |
| Message Chains | Code navigating `a.getB().getC().getD()` — talk to `a` only (Law of Demeter) |
| Repeated switch/if on type | An interface + polymorphism (often Strategy) should replace it |
| Refused Bequest | A subclass throws/disables an inherited method — inheritance was the wrong tool here |
| Fat Interface | One interface bundling unrelated behaviors, forcing implementers to stub methods they don't need — split it (Interface Segregation) |

## SOLID as design questions, not rules to recite

When designing or reviewing a class, ask:

- **S**RP — does this class have more than one reason to change?
- **O**CP — can I add a new variant (weapon, spell, creature behavior)
  without modifying existing classes, only adding a new one?
- **L**SP — can any implementation of a contract replace another without
  surprising the caller?
- **I**SP — does this interface force an implementer to depend on behavior
  it doesn't need?
- **D**IP — does this class depend on a concrete implementation
  (`SwordAttack`) or on a stable contract (`AttackBehavior`)? Depend on the
  contract — "plug in" implementations behind an interface rather than
  "soldering" a class to a concrete one.

## Domain-Driven Design

- **Ubiquitous language**: code should use the same words the game rules
  use. If the spec says "dégâts"/"damage", the method is `takeDamage()`, not
  a generic number mutation. Keep the mapping between French discussion
  and English (or French) code consistent and explicit.
- **Entity vs. Value Object** — ask: if two instances have identical
  values, are they the same thing?
    - **Entity**: defined by identity, not by current state (a `Hero`/
      `Character`, a `Game`, likely a `Creature` instance in combat — two
      identical goblins are still two different creatures).
    - **Value Object**: defined by its value, ideally immutable, replaced
      rather than mutated (`CharacterName`, `Health`, an ability score, a
      damage amount).
- **Invariants**: a rule that must always hold. Each invariant belongs to
  whichever object naturally has the information to protect it ("health
  never exceeds max" → `Health`; "a defeated character can't attack" →
  `Character`'s own behavior). Protecting a rule in exactly one logical
  place means the rest of the system can trust the object instead of
  re-validating everywhere.
- **Aggregates**: when a rule spans multiple objects ("a hero can only
  equip a weapon they own" spans `Hero` and `Inventory`), pick an
  **aggregate root** (likely `Hero`/`Character`) that the outside world
  talks to, and let it enforce cross-object consistency
  (`hero.equip(weapon)`, not
  `hero.getInventory().remove(weapon); hero.setEquippedWeapon(weapon)`
  from outside). Not everything related belongs to the same aggregate —
  `Game` can *reference* a list of `Character`s without owning their
  internal consistency. Aim for a boundary that's neither so small that
  rules scatter, nor so large it becomes a God Class for the whole domain.
- **Different views of the same concept**: not every module needs the full
  `Hero`. A lobby view might only need identity, name, and level — depending
  on the full object couples the lobby to changes that don't concern it.
  Don't assume one representation fits every consumer.
- **Factories for non-trivial construction**: when building a valid object
  requires real logic (apply species bonuses, compute derived stats,
  assemble starting equipment), put that in a Factory
  (`HeroFactory.create(args)`) rather than in the constructor. The entity
  stays focused on its own rules; the factory owns the assembly logic —
  separate responsibilities, separate reasons to change.

## Language convention

- **Code is in English**: class names, method names, variables, exceptions —
  everything in the source. So the Epic's French domain terms translate:
  `Guerrier` → `Warrior`, `Rôdeur` → `Ranger`, `Gobelin` → `Goblin`, etc.
  Keep the mapping to the Epic's French vocabulary explicit and consistent
  (a comment-free glossary in a design doc, not in the code) so the
  ubiquitous language still holds even across the translation.
- **Everything Git/process-facing is in French**: issue titles and
  descriptions, commit messages, branch names, PR titles and descriptions,
  and PR review comments. This applies on top of the formats above —
  Conventional Commits' `type(scope): description` keeps the English
  `type` keyword (`feat`, `fix`, etc.) but the description itself is in
  French, e.g. `feat(personnage): ajoute la validation du nom`. Same for
  branch names: `feature/12-validation-du-nom`, not
  `feature/12-name-validation`.

## Domain vocabulary (RPG spec)

Below the concepts keep their French Epic names for clarity in this
document; in code, use the English translation per the convention above.

- **Player / Hero**: a player creates heroes; a hero persists between
  combats (HP/effects don't reset automatically).
- **Lobby**: max 4 players, each picks one of their own heroes, then starts
  a Game.
- **Game**: the selected heroes become the Group. A game has at most one
  active Combat at a time. Game ends when the whole group is defeated.
- **Encounter**: a set of creatures to fight. Won encounters can't repeat in
  the same game.
- **Combat**: rounds of turns in initiative order. Tracks: current round,
  initiative order, whose turn it is. No participants added once combat
  starts.
- **Creature**: engine-controlled enemy. Not a Hero — no class, level, XP,
  inventory, or six ability scores.

### Key formulas (not exhaustive — the Epic PDF is the source of truth)

- **Ability modifier**: `floor((score - 10) / 2)`.
- **HP max (level 1)**: `class base HP + Constitution modifier`, min 1.
- **Attack roll**: `1d20 + ability modifier + proficiency bonus (+2 at
  level 1)` vs target AC. Natural 1 always misses; natural 20 always hits
  and doubles damage dice (not the flat bonus).
- **Mana**: never negative, never above max; insufficient mana blocks the
  spell.
- **Inventory**: max 10 items; can't equip/use what you don't own; can't
  add to a full inventory.
- **XP split**: integer division, remainder dropped.
- **Rest**: full HP + full mana + clears temporary effects; unusable
  mid-combat.

**Determinism requirement**: the team must be able to force/verify specific
outcomes (a d20 roll of 1 or 20, a known initiative, exact damage/healing) —
don't hardcode randomness in a way that can't be substituted later for
verification, once testing is covered in class.

### Out of scope for this version

No tactical map/positioning, no real-time or WebSockets, no PvP, no
crafting, no full economy. Don't build for these; don't let their absence
block good extensibility elsewhere.

## Git workflow

- **Branches**: `main` = always stable/deployable; `develop` = integration
  line for finished features (if the team uses Git Flow, as recommended).
  Feature branches: `feature/<issue-number>-short-description`, branched
  from `develop`, short-lived, merged back into `develop`.
- **Commits — Conventional Commits**: `type(scope): short imperative
  summary`, no period, 50–72 chars. Types: `feat`, `fix`, `docs`, `style`,
  `refactor`, `perf`, `test`, `chore`. One logical change per commit. Body
  explains *why*, not *how* — the diff already shows how. Reference the
  issue when relevant (`Closes #1234`).
- **Before opening a PR**: code compiles, branch is rebased on top of
  current `develop`, commits follow Conventional Commits, the PR is small
  and focused on one subject (ideally under ~400 changed lines — a small PR
  gets reviewed in 10 minutes, a giant one waits days).
- **PR description**: what changed and *why* (not just a line-by-line diff
  recap), how to test it, and `Closes #<issue>`. Use the `pr-description`
  skill for this.
- **Reviews — at least one approval required before merge** (per the
  course's team requirements). As author: don't take feedback personally,
  respond to every comment, keep the branch updated, don't force-push over
  commits mid-review (push new commits on top instead). As reviewer: name
  the specific line and the principle at stake (e.g. "this violates the Law
  of Demeter because...") rather than a vague "this feels off"; distinguish
  blocking issues from nitpicks (`nit:`); don't block for perfection.
- **Rebase, don't merge, to update a feature branch**: `git fetch origin`
  then `git rebase origin/develop` keeps history linear and surfaces
  conflicts early, while you still have context. **Never rebase a shared
  branch** (`main`, `develop`, or any branch someone else is using) —
  rebase only your own feature branch. Push with
  `git push --force-with-lease`, never bare `--force`. If a branch stays
  active more than a day, rebase at least once a day rather than letting it
  drift.
- **Merging into `develop`**: prefer squash merge for a clean history
  (matches "Squash and merge" as GitHub's recommended default) unless the
  team agrees otherwise.

## Team process (graded — don't bypass)

- Short-lived branches, one PR per change, **at least one approval** before
  merging.
- No single person owns one layer/part of the system for the whole
  session — rotate.
- Every member must be able to explain the overall architecture and any
  code merged, including AI-assisted code. Don't accept a generated diff you
  can't explain.
- When a domain rule is ambiguous, that's a signal to ask the client
  (professor), not to guess or let AI invent the answer.
- Available skills for this repo: `pr-description`, `pr-review`,
  `write-issue`, `commit-message`, `clean-code-refactor`,
  `java-conventions`, `architecture-decisions` — use them for their
  respective tasks; keep `commit-message` and `pr-description` aligned with
  the Conventional Commits format above.

## Current scope reminders

- **No tests yet** (not covered in class) — don't add a test framework
  unprompted.
- **No API/HTTP layer yet** — focus is the domain model: identity, name and
  its rules, six ability scores and modifiers, species/class bonuses, HP,
  mana, level/XP, a clear creation strategy (e.g. a `HeroFactory`),
  inventory and its capacity, weapons, armor, potions, equip rules
  (including class restrictions). Don't build the REST API, persistence,
  full lobby, combat engine, turn system, or Kanban/game loop yet — that
  comes in later sessions.
- Be ready to explain, for whatever's built so far: which classes are
  Entities vs. Value Objects, which rules are invariants and where they're
  enforced, how the boundary between `Hero`, `Inventory`, and `Equipment`
  was drawn, and where an interface/composition/Factory was used to limit
  the spread of change.
- Fill in `java-conventions/SKILL.md` and `architecture-decisions/SKILL.md`
  placeholders once the team agrees on naming/package structure.