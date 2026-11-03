# Domain rules — full ruleset from the Epic

Referenced from `CLAUDE.md`. Read this file whenever a task touches game
rules, stats, formulas, or anything the Epic defines — character creation,
combat, spells, items, XP, encounters.

---

## Domain rules (full ruleset from the Epic)

This section is the authoritative in-session copy of the game rules — treat
it as the source of truth over memory, but defer to the actual Epic PDF
(`/Users/germanviter/Desktop/Projet_de_session_API_RPG_A26.pdf`) if something here looks inconsistent
with it, and flag the discrepancy. Where the Epic leaves a case ambiguous,
that's a signal to ask the client (professor), not to invent a rule — see
**Team process** in `CLAUDE.md`.

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
(see **Prefer composition over inheritance** in `CLAUDE.md`).

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
event log.

**Actions vs. state** (ties back to **Core architectural principle** in
`CLAUDE.md`):
the API should express game actions ("Thorin attacks the Orc"), never let a
client replace a domain action with a direct state edit ("the Orc's HP is
now 4"). Same principle for healing, spells, items, equipment, turns, and
combat progression. See `docs/rest-api-guide.md` for how this maps to
concrete resources, URIs, and HTTP verbs.

**Error handling**: when an action can't be done, the API must let the
client understand why, without exposing internal technical details.
Examples: player/hero not found, invalid or already-used name, lobby full,
item missing, inventory full, incompatible equipment, insufficient mana,
spell unavailable, wrong turn, invalid target, hero out of combat, combat
already over, or an action forbidden in the current game state. See
**Domain exceptions → HTTP status codes** in `docs/rest-api-guide.md` for
how these get translated at the API boundary.

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