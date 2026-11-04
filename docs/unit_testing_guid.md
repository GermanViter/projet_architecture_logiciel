# Unit testing guide

Referenced from `CLAUDE.md`. Read this file whenever a task involves
writing, reviewing, or explaining a unit test — or whenever new domain code
is added and should come with tests alongside it (see
[Current scope reminders](../CLAUDE.md#current-scope-reminders) for
whether tests are expected yet for a given piece of work).

**Core idea, stated once**: a test verifies the **behavior** an application
must have — a story's acceptance criteria — not how the code happens to be
written. If the code changes but the behavior doesn't, the test shouldn't
change either.

---

## Why this matters: a one-character bug

Say the rule is "a hero's inventory holds at most 10 items." The check is
`if (itemCount >= MAX_ITEMS) { throw ... }`. Someone "tightens" it to `>`,
thinking it's safer. Everything compiles. Adding a 5th item to an empty
inventory works fine — looks correct. But adding the **10th** item (which
should be allowed — the inventory isn't full yet until that item is in) now
gets refused. Nobody manually tries "exactly at the limit." A test would
catch this on every single change, instantly.

A **unit test** is three things:
1. **An action** — do something to one class, directly in Java, no server,
   no database, in milliseconds.
2. **An expected result** — what should happen.
3. **A comparison** — does the actual result match?

"Unitaire" = one unit: one class, called directly. Hundreds of these run in
under a second, which is exactly why they're worth writing liberally.

### Tests verify acceptance criteria, not code

A story like "As a player, I want to create a hero, so that I can join a
game" comes with acceptance criteria — "the name can't be blank," "ability
scores must be between 3 and 18," "starting HP is never 0 or less," etc.
Each criterion becomes one or more tests, usually one per **boundary** (see
below). The test's name should read like the criterion.

| Acceptance criterion | Test(s) |
|---|---|
| Ability score is 3–18 | `abilityScore_belowMinimum_isRefused`, `abilityScore_atMinimum_isAccepted`, `abilityScore_atMaximum_isAccepted`, `abilityScore_aboveMaximum_isRefused` |
| Hero name follows the naming rule | `heroName_blank_isRefused`, `heroName_containsDigit_isRefused` |
| Inventory holds at most 10 items | `addItem_inventoryAtCapacity_isRefused`, `addItem_inventoryBelowCapacity_isAccepted` |

---

## The smallest test possible: Given, When, Then

Every test has three parts, whether or not they're commented as such:

```java
class AbilityScoreTest {

    private static final int MINIMUM_SCORE = 3;

    @Test
    void abilityScore_belowMinimum_isRefused() {
        int belowMinimum = MINIMUM_SCORE - 1;                    // Given

        assertThrows(InvalidAbilityScoreException.class,         // When + Then
                () -> new AbilityScore(belowMinimum));
    }
}
```

- **Given**: `belowMinimum` — the setup.
- **When**: `() -> new AbilityScore(belowMinimum)` — a lambda, not called
  directly. If it were called directly, the thrown exception would crash
  the test before the assertion ever ran.
- **Then**: `assertThrows` calls the lambda itself, catches the exception,
  and checks it's the right type.

**File location**: the test class mirrors the tested class's package, under
`src/test/java/...` instead of `src/main/java/...`, named
`<ClassName>Test`.

**No magic values, even in a test.** `MINIMUM_SCORE` is named, not
hardcoded — the test reads like the rule it checks.

### See it fail once — on purpose

A test you've never watched fail proves nothing (it might be checking
nothing at all, or checking the wrong thing). The first time a test is
written: run it (green), then deliberately break the rule it's protecting
(e.g. change the `<` to `<=` that it guards), run it again (red — and read
*why* it failed), then put the code back. Only after seeing both colors do
you know the test actually tests something.

### Two tools for the Then

- **`assertThrows`** (JUnit) — when the expected result is an exception.
- **`assertThat(...)`** (Truth, or AssertJ if the team prefers it) — when
  the expected result is a value. Reads like a sentence
  (`assertThat(score.value()).isEqualTo(MINIMUM_SCORE)`) and gives clear
  failure messages.

```java
@Test
void abilityScore_atMinimum_isAccepted() {
    AbilityScore score = new AbilityScore(MINIMUM_SCORE);

    assertThat(score.value()).isEqualTo(MINIMUM_SCORE);
}
```

---

## Bugs live at the boundaries

Testing a "normal" value (say, a score of 10) almost never catches a bug —
`>=` vs `>` only disagrees exactly at the edge. For **every** rule with a
limit, write a test **just below** the limit and a test **exactly at** the
limit:

| Case | Expected |
|---|---|
| Just below the limit | Refused |
| Exactly at the limit | Accepted (this is the one `<` vs `<=` bugs break) |
| A normal, comfortably-valid value | Accepted (rarely catches anything — write it for coverage, not for bug-finding) |

For a rule with **two** boundaries (a minimum *and* a maximum, like ability
scores being 3–18), that's four tests: just-below-min, at-min, at-max,
just-above-max.

```java
private static final int MINIMUM_SCORE = 3;
private static final int MAXIMUM_SCORE = 18;

@Test void abilityScore_belowMinimum_isRefused() { /* MINIMUM_SCORE - 1 */ }
@Test void abilityScore_atMinimum_isAccepted()   { /* MINIMUM_SCORE */ }
@Test void abilityScore_atMaximum_isAccepted()   { /* MAXIMUM_SCORE */ }
@Test void abilityScore_aboveMaximum_isRefused() { /* MAXIMUM_SCORE + 1 */ }
```

Compute the boundary from the named constant (`MAXIMUM_SCORE + 1`), never
write the literal (`19`) — if the limit changes, the test still describes
the right case.

### Many values, same expected result: `@ParameterizedTest`

When several different inputs should all produce the same outcome, write
the test once:

```java
@ParameterizedTest
@NullAndEmptySource
@ValueSource(strings = {"   ", "A1", "Gandalf!", "_Thorin_"})
void heroName_invalid_isRefused(String invalidName) {
    assertThrows(InvalidHeroNameException.class,
            () -> new HeroName(invalidName));
}
```
`@NullAndEmptySource` adds `null` and `""` automatically; `@ValueSource`
lists the rest. JUnit runs the method once per value and reports each one
separately — four+ cases, one test to read and maintain.

---

## The Given: fixtures

Constructing a valid domain object by hand, in every test, is repetitive
and buries the one value a test actually cares about in a pile of
boilerplate:

```java
// Repeated in every test — which value here actually matters?
new Hero(new HeroName("Thorin"), Species.DWARF, HeroClass.WARRIOR,
        new AbilityScores(15, 12, 16, 8, 10, 10));
```

A **fixture** names every value once and exposes a ready-made valid object:

```java
public final class HeroFixture {
    public static final HeroName HERO_NAME = new HeroName("Thorin");
    public static final Species SPECIES = Species.DWARF;
    public static final HeroClass HERO_CLASS = HeroClass.WARRIOR;
    public static final AbilityScores ABILITY_SCORES =
            new AbilityScores(15, 12, 16, 8, 10, 10);

    private HeroFixture() {}

    public static Hero aHero() {
        return new Hero(HERO_NAME, SPECIES, HERO_CLASS, ABILITY_SCORES);
    }
}
```

Tests then read:
```java
private final Hero hero = aHero();
```
The constants are **public** so tests can compute their own cases from
them (e.g. `ABILITY_SCORES.strength() + 1`) instead of hardcoding a new
value that happens to disagree with the fixture.

**A fixture is always**: a `final` class, living in a `fixtures/` folder
next to the tests for that package, private constructor, public named
constants, and one or more `aXxx()` methods returning a valid object. No
logic beyond assembling the object.

**One fixture per domain object.** Example layout (adapt the base package
to whatever the team settles on in `java-conventions`):

```
src/test/java/<base-package>/
  hero/domain/
    HeroNameTest  AbilityScoreTest  HealthTest  InventoryTest  ...
    fixtures/
      HeroFixture  HeroNameFixture  AbilityScoresFixture
      HealthFixture  InventoryFixture
  combat/domain/
    CombatTest  InitiativeTest  ...
    fixtures/
      CombatFixture  CreatureFixture  ClockFixture
```

Why: every test's data lives at exactly one place. If a rule's reference
value changes (say the default ability scores in the fixture), every test
built on it follows automatically — nothing to hunt down across dozens of
files.

---

## Time (and other non-deterministic input)

`LocalDateTime.now()` (or any live randomness) inside domain code makes
tests depend on *when* they happen to run — a test that's green today can
turn red next month, and "yesterday" can't be tested reliably at all.

**Fix: inject it.** Anything the code doesn't control — the clock, dice
rolls — gets passed in through the constructor rather than fetched live:

```java
public ReservationFactory(Clock clock) { ... }
```
Production wires the real clock (`Clock.systemDefaultZone()`); a test wires
a **frozen** one:

```java
public final class ClockFixture {
    public static final Instant NOW = Instant.parse("2026-11-14T12:00:00Z");
    public static final Clock FIXED_CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
}
```
Every test using `FIXED_CLOCK` gets the exact same "now," forever — the
test still passes in ten years.

**This is the same technique the project's determinism requirement for
dice rolls needs** (see `docs/domain-rules.md` → Dice / Determinism
requirement): don't call a random source directly inside combat code.
Define a small port, e.g. `DiceRoller { int roll(int sides); }`, inject it
the same way `Clock` is injected, use a real random implementation in
production, and in tests supply a fixed/stub implementation that returns a
chosen value (a natural 20, a natural 1, a specific damage roll) so a
critical hit, a miss, or an exact damage amount can be tested directly
instead of hoped for.

---

## Testing a use case: mocking its ports

A use case like `CreateHeroUseCase` depends on a **port** — an interface
such as `HeroRepository` — not a concrete implementation. In production,
the installer wires `InMemoryHeroRepository`. In a test, wire a **mock**
instead: a fake object (built by Mockito) that implements the interface
and does nothing until told what to do.

```java
private final HeroRepository repository = mock(HeroRepository.class);
private final CreateHeroUseCase useCase =
        new CreateHeroUseCase(new HeroFactory(), repository, new HeroOutputMapper());
```

This is only possible *because* the use case depends on an interface
(`HeroRepository`), not a concrete class — Dependency Inversion again. A
bug in the real repository can't make this test fail: only the use case's
own logic is under test.

### Telling the mock what to do (Given), and checking what it was asked (Then)

```java
// Given — tell it what to return
when(repository.fetchById(HERO_ID)).thenReturn(aHero());
// ...or what to throw:
when(repository.fetchById(OTHER_ID)).thenThrow(new HeroNotFoundException(OTHER_ID));

// Then — check what the use case asked of it
verify(repository).save(hero);                      // called exactly once, with this argument
verify(repository, never()).fetchById(HERO_ID);      // never called
```
Without `when(...)`, a mock returns `null` for anything it wasn't told
about. `verify(...)` doesn't re-run the use case — it inspects what the
use case already did to the mock while the test's `When` step ran.

**Mock only the ports** — what a use case reaches *outside its own module*
for (a repository, a gateway to another module). Keep the **real** domain
objects in the test: the factory, the entity, value objects, mappers —
these are simple, side-effect-free, and are exactly the behavior the test
is supposed to verify. Mocking the domain means testing the mock — the
test would stay green even if the actual business rule were wrong.

### Varying the input: a command builder

When a use case's tests need to vary one field at a time (a valid command,
one with an invalid name, one with an invalid ability score...), a fixture
alone isn't enough — use a **builder**, with defaults pulled from the
relevant fixtures and named intention methods instead of raw values:

```java
public class CreateHeroCommandBuilder {
    private String name = HeroNameFixture.HERO_NAME.value();
    private int strength = AbilityScoresFixture.STRENGTH;
    // ...other fields, defaulted from fixtures

    public static CreateHeroCommandBuilder aCreateHeroCommand() {
        return new CreateHeroCommandBuilder();
    }

    public CreateHeroCommandBuilder withInvalidName() {
        this.name = "";
        return this;
    }

    public CreateHeroCommandBuilder withInvalidStrength() {
        this.strength = AbilityScoreFixture.MINIMUM_SCORE - 1;
        return this;
    }

    public CreateHeroCommand build() {
        return new CreateHeroCommand(name, strength, /* ... */);
    }
}
```
Write `.withInvalidStrength()`, never `.withStrength(2)` — the invalid
value is computed from the rule's own constant, so it stays correct if the
rule changes.

### Putting it together

```java
@Test
void execute_validCommand_savesTheHero() {
    CreateHeroCommand command = aCreateHeroCommand().build();

    useCase.execute(command);

    ArgumentCaptor<Hero> saved = ArgumentCaptor.forClass(Hero.class);
    verify(repository).save(saved.capture());
    assertThat(saved.getValue().name()).isEqualTo(HeroNameFixture.HERO_NAME);
}

@Test
void execute_invalidStrength_isRefusedAndNothingIsSaved() {
    CreateHeroCommand command = aCreateHeroCommand().withInvalidStrength().build();

    assertThrows(InvalidAbilityScoreException.class,
            () -> useCase.execute(command));
    verify(repository, never()).save(any(Hero.class));
}
```
`ArgumentCaptor` grabs the object the use case passed to the mock, so its
*contents* can be asserted on. The second test checks two things at once
on purpose: the exception propagates to the caller, **and** nothing got
saved before it was thrown — a use case that saves first and validates
after would be caught here.

---

## Good practices

### No magic values

| Avoid | Prefer |
|---|---|
| `new AbilityScore(2)` | `new AbilityScore(MINIMUM_SCORE - 1)` |
| `.withStrength(2)` | `.withInvalidStrength()` |
| `assertThat(name).isEqualTo("Thorin")` | `assertThat(name).isEqualTo(HeroNameFixture.HERO_NAME)` |

A magic value in a test hides *why* that value was chosen, and if the rule
changes, every test using it needs a manual fix. Three legitimate homes for
a value instead: a **named constant** describing the rule
(`MINIMUM_SCORE - 1`), a **fixture** (`HeroNameFixture.HERO_NAME`), or a
**builder intention method** (`withInvalidStrength()`).

### FIRST — five qualities of a good unit test

| | | |
|---|---|---|
| **F**ast | No server, no database — milliseconds. |
| **I**ndependent | No test depends on another test's state or order. |
| **R**epeatable | Same result tomorrow and in ten years (`Clock.fixed`, injected dice). |
| **S**elf-validating | The test itself says pass or fail — no reading logs by hand. |
| **T**imely | Written alongside the code, not "added later." |

### What to test, what not to

**Test**: the behavior a story's acceptance criteria describe — business
rules and their boundaries, what the application must do
(`inventory.addItem(...)` refuses past capacity), anything time-dependent
(with a fixed clock).

**Don't test directly**: generated getters and records, private methods
(they're exercised through the public method that calls them), the
language or its libraries (`LocalTime` itself works — that's not this
project's job to verify), and never the same case twice under different
names.

### Pitfalls

- **A test with no assertion** — it always passes, because it checks
  nothing.
- **Magic values** — see above.
- **`now()` (or raw `Random`) inside the code or the test** — green today,
  red tomorrow; use the injected `Clock`/`DiceRoller` instead.
- **Recomputing the production formula inside the test** — if the test
  derives its expected value with the same logic as the code under test, a
  bug in that logic passes both. Use a known, hand-picked expected value
  instead.
- **Shared mutable (`static`) test state** — results start depending on
  test execution order.
- **One test asserting several unrelated behaviors** — a failure doesn't
  say which one broke. One behavior per test.

### Checklist before opening a PR with new domain code

- Every test has a visible Given, When, and Then.
- Every rule with a limit has a just-below and an at-the-limit test.
- Every domain object used in tests has a fixture; no raw values scattered
  in test bodies.
- The Given comes from a fixture, a builder, or a small helper method —
  never hand-assembled inline.
- No `now()`, no live randomness, no real repository/database in a unit
  test — a fixed clock and mocked ports instead.
- The test's name describes the expected behavior, not the implementation.
- Each new test has been watched failing once, for the right reason.
- Every acceptance criterion in the related issue has at least one test.

---

## Where this fits: the base of the pyramid

Unit tests are the base: fast, numerous, precise — this is where every
rule's boundaries get checked. Integration tests and tests that exercise
the whole application (through the actual API, see `docs/rest-api-guide.md`)
sit above this, are slower, and are fewer in number; they're a later
topic, not yet covered.

---

## Appendix: dependencies and running tests

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.2</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.google.truth</groupId>
    <artifactId>truth</artifactId>
    <version>1.4.1</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
```
`junit-jupiter` also brings `@ParameterizedTest`. `mockito-junit-jupiter`
brings `mock`/`when`/`verify`. Truth is one option for `assertThat(...)` —
AssertJ is equivalent if the team prefers it; pick one and stay consistent.

- **In IntelliJ**: the green triangle next to a test class or a single
  `@Test` method.
- **From the command line**: `mvn test` runs unit tests
  (`src/test/java/...`). (A later `mvn verify`-style end-to-end suite, if
  the team adds one, belongs in a separate `src/e2e` — not covered yet.)