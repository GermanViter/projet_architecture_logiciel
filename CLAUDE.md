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
directly — always through a domain action. See
[Using the API](#using-the-api) for how this plays out at the API boundary.

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
`AbilityScore`, `ArmorClass`, etc. — small classes that own their own
validation and let the containing class delegate to them
(`Character.takeDamage()` calls `health.takeDamage()`) rather than
reimplementing the rule.

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
interface + polymorphism was probably called for instead. This is directly
useful for **species, classes, weapons, spells, and creature behaviors**
below — all are explicitly meant to grow in future versions, so none of
them should be a hardcoded `if/switch` chain.

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
  Prefer a domain exception (`InvalidCharacterNameException`,
  `InsufficientManaException`, `InventoryFullException`) over a generic one
  (`IllegalArgumentException`) — it lets calling code react to the specific
  rule that was violated, and the exception's type documents which rule
  that was.
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
  and English code consistent and explicit (see
  [Language convention](#language-convention)).
- **Entity vs. Value Object** — ask: if two instances have identical
  values, are they the same thing?
  - **Entity**: defined by identity, not by current state (a `Hero`/
    `Character`, a `Game`, a `Combat`, a `Creature` instance in combat — two
    identical goblins are still two different creatures).
  - **Value Object**: defined by its value, ideally immutable, replaced
    rather than mutated (`CharacterName`, `Health`, `AbilityScore`, a
    damage amount, `ArmorClass`).
- **Invariants**: a rule that must always hold. Each invariant belongs to
  whichever object naturally has the information to protect it ("health
  never exceeds max" → `Health`; "a defeated character can't act" →
  `Character`'s own behavior). Protecting a rule in exactly one logical
  place means the rest of the system can trust the object instead of
  re-validating everywhere.
- **Aggregates**: when a rule spans multiple objects ("a hero can only
  equip a weapon they own and their class allows" spans `Hero` and
  `Inventory`), pick an **aggregate root** (likely `Hero`/`Character`) that
  the outside world talks to, and let it enforce cross-object consistency
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
  `Guerrier` → `Warrior`, `Rôdeur` → `Ranger`, `Mage` → `Mage`, `Clerc` →
  `Cleric`, `Gobelin` → `Goblin`, `Orc` → `Orc`, `Troll` → `Troll`, etc.
  Keep the mapping to the Epic's French vocabulary explicit and consistent
  (a comment-free glossary in a design doc, not in the code) so the
  ubiquitous language still holds even across the translation.
- **Everything Git/process-facing is in French**: issue titles and
  descriptions, commit messages, branch names, PR titles and descriptions,
  and PR review comments. This applies on top of the formats in
  [Git workflow](#git-workflow) — Conventional Commits' `type(scope):
  description` keeps the English `type` keyword (`feat`, `fix`, etc.) but
  the description itself is in French, e.g.
  `feat(personnage): ajoute la validation du nom`. Same for branch names:
  `feature/12-validation-du-nom`, not `feature/12-name-validation`.

---

## Domain rules (full ruleset from the Epic)

This section is the authoritative in-session copy of the game rules — treat
it as the source of truth over memory, but defer to the actual Epic PDF
(`/Users/germanviter/Desktop/Projet_de_session_API_RPG_A26.pdf`) if something here looks inconsistent
with it, and flag the discrepancy. Where the Epic leaves a case ambiguous,
that's a signal to ask the client (professor), not to invent a rule — see
[Team process](#team-process-graded--dont-bypass).

### Players, heroes, and the lobby

- A player creates heroes before playing. To play, a player joins a
  **lobby** (max 4 players); each player selects one of their own
  previously-created heroes.
- Once all players are ready, the game starts; the selected heroes become
  the **Group** for that adventure.
- The group explores, has encounters, fights creatures, gets equipment,
  gains XP, levels up, and rests between fights.
- A game has **at most one active combat at a time**.
- If the whole group is defeated, the game ends.
- Game state must be retrievable across API calls (in-memory, but
  consistent between requests while the server runs).

### Creating a hero

On creation, a player picks: name, species, class, and the six ability
scores. These choices determine stats, equipment, and capabilities.

All new heroes start at:
- **Level 1**, **0 XP**, full HP, full mana (if their class uses magic).

A hero's state carries over between fights — ending one fight injured means
starting the next one injured too (no automatic full heal between fights
except via an explicit **Rest**, see below).

**Hero name rules**: 2–30 characters; letters and spaces only; can't start
or end with a space; must be unique system-wide.
- Valid: `Aria`, `Thorin`, `Eldrin`, `Jean Pierre`
- Invalid: `Mage123`, `Thorin!`, `_Gandalf_`, `Player#42`

**Ability scores**: Force/Strength, Dextérité/Dexterity,
Constitution/Constitution, Intelligence/Intelligence, Sagesse/Wisdom,
Charisme/Charisma. Player assigns 3–18 to each at creation; species
bonuses/penalties apply after; a score can never exceed 20.

**Ability modifier**: `floor((score - 10) / 2)`.

| Score | Modifier |
|---|---|
| 3 | -4 |
| 4–5 | -3 |
| 6–7 | -2 |
| 8–9 | -1 |
| 10–11 | 0 |
| 12–13 | +1 |
| 14–15 | +2 |
| 16–17 | +3 |
| 18–19 | +4 |
| 20 | +5 |

Modifiers are used throughout: attacks, damage, initiative, armor class, HP,
and some spells.

### Species (v1): Human, Elf, Dwarf, Orc

| Species | Bonus / penalty |
|---|---|
| Human | +1 to all six ability scores |
| Elf | +2 Dexterity, +1 Intelligence |
| Dwarf | +2 Constitution, +1 Strength |
| Orc | +2 Strength, +1 Constitution, -1 Intelligence |

More species are planned for future versions — design for easy addition
(see [Composition over inheritance](#prefer-composition-over-inheritance)).

### Classes (v1): Warrior, Ranger, Mage, Cleric

| Class | Base HP | Mana | Starting equipment | Dominant ability |
|---|---|---|---|---|
| Warrior (Guerrier) | 12 | 0 | Long sword, chainmail | Strength |
| Ranger (Rôdeur) | 10 | 0 | Bow, leather armor | Dexterity |
| Mage | 6 | 10 | Staff, no armor | Intelligence |
| Cleric (Clerc) | 10 | 8 | Mace, chainmail | Wisdom |

**Equipment restrictions by class**:

| Class | Can use |
|---|---|
| Warrior | Melee weapons; light and heavy armor |
| Ranger | Bows, daggers, swords; light armor |
| Mage | Staves, daggers; no armor in v1 |
| Cleric | Maces, staves; light and heavy armor |

### HP and mana

- **HP max at level 1** = class base HP + Constitution modifier, minimum 1.
  At creation, current HP = max HP.
- Current HP never negative, never exceeds max.
- At 0 HP, the hero is **out of combat** and can't act; can still receive
  healing; becomes active again starting at 1 HP.
- Example: a Warrior (12 base HP) with 16 Strength... no — with 16
  **Constitution** (modifier +3) has 15 max HP at level 1.

- **Mana** (Mage and Cleric only): consumed when a spell is cast; never
  negative, never exceeds max; a spell can't be cast without enough mana.

| Class | Mana max at level 1 |
|---|---|
| Warrior | 0 |
| Ranger | 0 |
| Mage | 10 |
| Cleric | 8 |

### Inventory and equipment

- Each hero has their own inventory: max **10 items**; each copy counts as
  one item; must own an item to use/equip it; can't add to a full
  inventory.

**Weapons**:

| Weapon | Damage | Ability |
|---|---|---|
| Long sword | 1d8 | Strength |
| Axe (Hache) | 1d10 | Strength |
| Mace | 1d6 | Strength |
| Dagger | 1d4 | Dexterity |
| Bow | 1d8 | Dexterity |
| Staff | 1d6 | Strength |

Only one main weapon equipped at a time; must own it and class must permit
it. Changing weapon is free outside combat; in combat it costs the main
action.

**Armor**:

| Armor | Armor Class |
|---|---|
| No armor | 10 + Dexterity modifier |
| Leather armor | 11 + Dexterity modifier |
| Chainmail | 16 (no Dexterity bonus) |

Only one main armor equipped at a time; must own it and class must permit
it.

**Healing potion**: heal = `2d4 + 2` HP, capped at max HP. Consumed on use.
In combat, using a potion costs the main action.

### Exploration and encounters

No map/movement system in v1 — the group accesses available
**encounters** directly. An encounter is a set of creatures to fight. A
won encounter can't be repeated in the same game.

| Encounter | Composition |
|---|---|
| Goblin patrol (Patrouille gobeline) | 3 Goblins |
| Orc camp (Campement orc) | 2 Orcs, 1 Goblin |
| Troll's den (Antre du Troll) | 1 Troll |

### Creatures

Creatures are engine-controlled; they don't necessarily have a class,
level, XP, inventory, or the six ability scores the way heroes do.

**Goblin** — HP 7, AC 12, Initiative +2, XP 50. Attack: Rusty dagger
`+3`, damage `1d6 + 1`.
Behavior: attacks the active hero with the **lowest** current HP.

**Orc** — HP 15, AC 13, Initiative +1, XP 100. Attack: Axe `+4`, damage
`1d8 + 2`.
Behavior: attacks the active hero with the **highest** current HP.

**Troll** — HP 35, AC 15, Initiative 0, XP 300. Attack: Club `+6`,
damage `2d6 + 3`.
Behavior: attacks a **random** hero among those still able to fight.

### Turn-based combat

Participants belong to two sides: heroes and creatures, fixed before combat
starts — no one can be added once combat begins. Combat runs in rounds
until one side can no longer fight.

**Initiative**: Hero = `1d20 + Dexterity modifier`; Creature =
`1d20 + initiative bonus`. Participants are ranked highest to lowest; this
order holds for the whole combat.

**Rounds and turns**: round 1 is first. Every participant still able to
fight acts once per round, in initiative order; defeated participants are
skipped. The engine must always be able to report: current round,
initiative order, and whose turn it is. Example order: Eldrin → Goblin A →
Thorin → Orc → Goblin B, then back to Eldrin for the next round.

**Main action**: during their turn, a hero normally has one main action:
attack, cast a spell, use a potion, change weapon, or defend. Once used, no
second action that turn.

### Attacks and damage

**Attack roll (hero)** = `1d20 + weapon's ability modifier + proficiency
bonus` (proficiency bonus is +2 at level 1). Compare to target's AC; hits
if ≥ AC.
Example: a Warrior with 16 Strength (+3) using a long sword at level 1
rolls `1d20 + 3 + 2`.

- **Natural 1** always misses, regardless of bonuses.
- **Natural 20** always hits and is a **critical hit**: the number of
  damage **dice** is doubled, the flat bonus is not (`1d8 + 3` →
  `2d8 + 3`).

**Damage**: on a hit, the target takes damage. For a hero, the weapon's
ability modifier is added (e.g. long sword with +3 Strength = `1d8 + 3`).
Creatures use the flat values in their own definition. Current HP never
goes negative.

**Defend**: a hero can spend their main action to defend, gaining **+2 AC**
until the start of their next turn, after which the bonus disappears
automatically.

### Spells

Only some classes cast spells. A spell has a mana cost, a targeting rule,
and an effect. Future spells may target one/many enemies, one/many allies,
or the caster, and may damage, heal, buff, debuff, or apply other effects —
design the spell system so adding a new spell/targeting/effect doesn't
require modifying existing spells (Strategy/interface, not a growing
switch).

| Spell | Class | Cost | Target(s) | Effect |
|---|---|---|---|---|
| Magic Missile (Projectile magique) | Mage | 2 | One enemy | `1d6 + Int. mod.`; auto-hits |
| Fireball (Boule de feu) | Mage | 4 | All enemy creatures | `2d6 + Int. mod.` to each; cost paid once |
| Heal (Soin) | Cleric | 3 | Cleric or another hero | `1d8 + Wis. mod.` HP; can target a hero at 0 HP |
| Bless (Bénédiction) | Cleric | 4 | All heroes still present | `+1 AC` until end of each hero's next turn |

### Temporary effects and dice

| Effect | Rule |
|---|---|
| Poisoned (Empoisonné) | `1d4` damage at the start of each of its turns; has a duration in turns |
| Stunned (Étourdi) | Can't act on its next turn; the turn is skipped, then the effect ends |
| Enhanced defense (Défense renforcée) | Temporarily raises AC — Defend applies +2 AC, Bless applies +1 AC |

More effects are planned for future versions.

**Dice**: d4, d6, d8, d10, d20. `2d6 + 3` = roll two six-sided dice, sum
them, add 3.

**Determinism requirement**: rolls are random in a normal game, but the team
must be able to **force/verify** known outcomes automatically — a d20 of 1
or 20, a known initiative, an exact damage or heal value. Don't hardcode
randomness in a way that can't be substituted later for verification, once
testing is covered in class.

### Creature behavior

When it's a creature's turn, the engine picks its action automatically.
Not all creatures decide the same way (see per-creature behaviors above).
Future versions may add: behavior changes when wounded, special abilities,
protecting/healing another creature, or targeting certain hero types —
design creature behavior as a pluggable strategy, not a single method with
growing conditionals.

### End of combat, XP, and progression

- Check for combat end after any action that could knock out a
  participant.
- Heroes win when all creatures are at 0 HP; creatures win when all heroes
  are at 0 HP. Once combat ends, no further combat actions, no new rounds.
  The result is kept.

**XP per creature**: Goblin 50, Orc 100, Troll 300. A won fight's total XP
is split **equally** among participating heroes; only the integer part of
the division is kept (`100 / 3 = 33` each, remainder dropped).

**Levels** (v1 caps at level 5):

| Level | Total XP required |
|---|---|
| 1 | 0 |
| 2 | 300 |
| 3 | 900 |
| 4 | 2700 |
| 5 | 6500 |

On level-up, max HP increases by `class base HP / 2 + Constitution
modifier`, minimum +1. Other level benefits may be added later.

**Rewards**: a won encounter may also grant potions, weapons, or armor;
players assign obtained items to heroes in their group (added to that
hero's inventory).

**Rest**: between fights, the group can rest — restores all HP, all mana,
and clears temporary effects. Can't be done mid-combat.

### Event log

The game keeps a readable history of important events — used by client
interfaces, debugging, stats, and future features. Example style (don't
reproduce verbatim — this illustrates the level of detail, not literal
text to copy):

> Combat begins → initiative rolled per participant → an attack roll
> described with the modifiers used → hit/miss and resulting damage →
> a spell cast and its effect → a participant knocked out → the fight's
> outcome.

### Bestiary and storage

The creature catalog ("bestiary") may eventually come from a JSON file, a
database, or an external service instead of being hardcoded — **combat
behavior must not change depending on where bestiary data comes from**.
This is a strong hint: define a bestiary access abstraction (interface) now
even though v1 implements it in-memory, so swapping the source later
doesn't touch combat code (OCP/DIP again).

All data (players, heroes, lobbies, games, hero state/progression,
equipment/inventory, finished encounters, the active combat) must be
retrievable across API calls for as long as the server runs. **v1 storage
is entirely in-memory** — no external database, no persistence across
restarts is required.

### Using the API

A client application must eventually be able to: create a player, create a
hero, list a player's heroes; create/join/leave a lobby; select a hero in a
lobby and start a game; view game and hero state; view/modify inventory and
equipment; view available encounters and start one; view a combat, its
initiative order, and whose turn it is; attack, cast a spell, use a potion,
change weapon, defend, and end a turn; collect rewards, rest, and view the
event log. Exact URIs, HTTP methods, and JSON shapes are defined later in
development — **not yet in scope**, see
[Current scope](#current-scope-reminders).

**Actions vs. state** (ties back to
[Core architectural principle](#core-architectural-principle-actions-not-state-mutation)):
the API should express game actions ("Thorin attacks the Orc"), never let a
client replace a domain action with a direct state edit ("the Orc's HP is
now 4"). Same principle for healing, spells, items, equipment, turns, and
combat progression.

**Error handling**: when an action can't be done, the API must let the
client understand why, without exposing internal technical details.
Examples: player/hero not found, invalid or already-used name, lobby full,
item missing, inventory full, incompatible equipment, insufficient mana,
spell unavailable, wrong turn, invalid target, hero out of combat, combat
already over, or an action forbidden in the current game state.

### Out of scope for this version

No tactical map/positioning, no precise distance/line-of-sight, no
graphical engine or full UI, no real-time/WebSockets, no PvP, no full
economy, no crafting. Don't build for these; don't let their absence block
good extensibility elsewhere.

### The three original User Stories (end of session)

Each team proposes and builds three original User Stories extending the
game beyond the Epic (~9 hours of outside-class work combined, not
necessarily equal in size). They must: introduce real new rules/behavior
(not cosmetic, not just new data with no new behavior), interact with
existing features, be exposed through the API where relevant, respect
existing rules, fit the existing architecture, and come with acceptance
criteria. They must be presented to the client (professor) **before**
being built, covering user need, business rules, interactions with
existing features, and acceptance criteria — and may be adjusted after
that discussion. (Example directions from the Epic: new class/species with
special rules, new spell mechanic, multi-phase boss, resistances/
vulnerabilities, a shield mechanic, fleeing/resurrection, pets/
environmental effects, a new equipment category, a new creature/behavior, a
new encounter/reward mechanic.)

---

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
  the Conventional Commits format above and the French language convention.

## Current scope reminders

- **No tests yet** (not covered in class) — don't add a test framework
  unprompted.
- **No API/HTTP layer yet** — focus is the domain model: identity, name and
  its rules, six ability scores and modifiers, species/class bonuses, HP,
  mana, level/XP, a clear creation strategy (e.g. a `HeroFactory`),
  inventory and its capacity, weapons, armor, potions, equip rules
  (including class restrictions). Don't build the REST API, persistence,
  full lobby, combat engine, turn system, or Kanban/game loop yet — that
  comes in later sessions, unless told otherwise.
- Be ready to explain, for whatever's built so far: which classes are
  Entities vs. Value Objects, which rules are invariants and where they're
  enforced, how the boundary between `Hero`, `Inventory`, and `Equipment`
  was drawn, and where an interface/composition/Factory was used to limit
  the spread of change.
- Fill in `java-conventions/SKILL.md` and `architecture-decisions/SKILL.md`
  placeholders once the team agrees on naming/package structure.