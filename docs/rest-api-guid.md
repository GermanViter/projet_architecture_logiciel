# REST API layer — HTTP + Jersey reference

Referenced from `CLAUDE.md`. Read this file whenever a task touches the
api layer: resources, DTOs, status codes, exception mappers, or anything
HTTP/Jersey-related.

---

## REST API layer (HTTP + Jersey)

This section covers the **api layer**: how the domain gets exposed over
HTTP. It builds directly on **Core architectural principle** in `CLAUDE.md`
and **Using the API** in `docs/domain-rules.md` — same rule, applied at
the wire level: the API expresses **actions on resources**, never direct state
edits, and **HTTP stops at the api layer** — the domain throws a
`RestaurantException`-style exception; it has no idea what a 400 is.

### Where HTTP fits in the layering

```
api             Jersey resources, DTOs + Bean Validation, Mappers, ExceptionMapper
application     Use cases, Commands, Outputs
domain          Entities, Value Objects, domain exceptions
infrastructure  Repositories (in-memory for v1; swappable later)
```

The dependency rule still holds: everything points toward the domain.
HTTP, JSON, Jersey, and Bean Validation are details of the **api** layer —
the domain never imports anything from `jakarta.ws.rs.*`. A domain object
never receives a DTO, and a use case never returns a `Response`.

### Resources are nouns, not verbs

The HTTP verb already says the action — a verb in the URI means it's RPC
dressed up as REST.

| Avoid | Prefer | Why |
|---|---|---|
| `/getHeroes` | `GET /heroes` | The verb already says "read" |
| `/createHero` | `POST /heroes` | Creating inside a collection |
| `/hero/42` | `GET /heroes/42` | Collections are plural |
| `/attack?targetId=7` | `POST /combats/{id}/attack` | The action is a sub-resource, the target is in the body |

Rules of thumb: plural nouns, lowercase, words separated by hyphens, at
most two levels of nesting (`/heroes/42/inventory` reads as "hero 42's
inventory"; past that it gets hard to read).

**Path vs. query parameter** — the test: if you remove the parameter, are
you still talking about the same resource? No → path (`@PathParam`,
identifies). Yes → query (`@QueryParam`, filters/sorts/paginates — optional,
omitting it just means "the whole collection").

**Stateless**: every request carries everything needed to handle it (who's
calling, what they want) — no server-side session remembered between
calls. The *resources'* state (a hero's HP, a combat's round) obviously
persists server-side; what's stateless is the *conversation*. In this
project a caller identifies itself however the team decides (a header, a
player id in the path) — decide and document it, the same way the slides'
demo uses an `Owner` header.

### HTTP verbs: five verbs, five intents

| Verb | Role | Body | Safe | Idempotent |
|---|---|---|---|---|
| GET | Read a resource or collection | no | yes | yes |
| POST | Create in a collection, trigger an action | yes | no | no |
| PUT | Replace a resource entirely | yes | no | yes |
| PATCH | Modify part of a resource | yes | no | not guaranteed |
| DELETE | Remove a resource | no | no | yes |

- **Safe** = doesn't modify anything on the server.
- **Idempotent** = repeating it 1 time or 10 times leaves the server in the
  same final state (useful when a response is lost and the client resends:
  a lost `DELETE` response is safe to retry — same end state, even if the
  2nd call returns 404 instead of 204; a lost `POST` response is **not**
  safe to retry without an idempotency key — resending can e.g. double an
  action).
- **PUT replaces, PATCH modifies.** With PUT, the body *is* the new
  resource — any field left out is lost (or reset to default). With PATCH,
  only the fields sent change.

**Don't expose setters as HTTP verbs.** A tempting but wrong move: `PATCH
/combats/{id}/participants/{heroId} { "hp": 4 }`, or even `PATCH
/reservations/7 { "status": "CANCELLED" }` — the client is choosing the
resource's new state directly, which is a setter exposed on the Web (not
Tell, Don't Ask) and can bypass domain rules (e.g. a cancellation-cutoff
rule). Prefer a `POST` on a sub-resource named after the action:
`POST /combats/{id}/attack`, `POST /combats/{id}/cast-spell`, `POST
/heroes/{id}/rest`. This is the API-layer form of `CLAUDE.md`'s core architectural
principle (actions, not state mutation) — each such endpoint calls a use case; the domain decides, not the client.

### Request and response anatomy

Both sides have three parts: a first line (verb + URI, or a status code),
headers (format, identity, location…), and — after a blank line — an
optional body.

- **`Content-Type`**: what I'm sending (applies to request and response —
  each describes its own body). Jersey: `@Consumes`.
- **`Accept`**: what I want to receive (request only — the client is
  asking). Jersey: `@Produces`.
- Mismatch → `415 Unsupported Media Type` (server can't read what you
  sent) or `406 Not Acceptable` (server can't produce what you asked for).
  A missing `Content-Type: application/json` header in a manual
  curl/Postman call is a common cause of an unexpected 415.

### Status codes

First digit tells the story: **2xx** success, **3xx** redirection (rare in
a JSON API), **4xx** client error (the client can fix its request and
retry), **5xx** server error (the client can't do anything about it).

**201 Created and 204 No Content** — both easy to forget:
- After a creation: `Response.created(location).build()` → `201 Created` +
  a `Location` header pointing at the new resource. Forgetting `Location`
  leaves the client unable to find what it just created.
- After a deletion (or an action with no result to return):
  `Response.noContent().build()` → `204 No Content`, never a body.
- Returning a DTO directly from a resource method makes Jersey answer
  `200` (or `204` if it returns `null`) — fine for a `GET`, not enough for
  a creation.

**The three pairs that get confused** — ask one question per pair:
| Pair | Question | Meaning |
|---|---|---|
| 400 Bad Request vs 401 Unauthorized | Can I even read the request? / Do I know who's calling? | 400: malformed request (missing field, unreadable value). 401: caller identity missing/invalid. |
| 404 Not Found vs 403 Forbidden | Does the resource exist? / Do they have the right? | 404: resource doesn't exist. 403: it exists, but this caller can't act on it. |
| 422 Unprocessable Content vs 409 Conflict | Well-formed but refused? / Blocked by current state? | 422: syntactically fine but rejected value (e.g. negative capacity). 409: valid request, but the resource's current state blocks it (e.g. full at that time). |

A simple, acceptable choice for this project (matching the reference demo):
respond `400` for any broken business rule by default, and only split out
a more specific code (e.g. `409` for "combat already over" or "inventory
full") where it's clearly useful to the client. Document whichever choice
the team makes.

### Domain exceptions → HTTP status codes

The **domain throws, the api layer translates** — this is the DIP
boundary: the domain never depends on Jersey or HTTP status codes.

| Exception | Thrown by | Likely code | Why |
|---|---|---|---|
| `HeroNotFoundException` | use case | 404 | The hero/resource doesn't exist |
| `InvalidCharacterNameException` | domain (`CharacterName`) | 400 | Name fails its own validation rule |
| `InsufficientManaException` | domain (`Hero`/`Mana`) | 400 or 409 | Not enough mana to cast — arguably a state conflict |
| `InventoryFullException` | domain (`Inventory`) | 409 | Inventory's current state blocks the action |
| `WrongTurnException` | domain/application (`Combat`) | 409 | Not this participant's turn right now |
| Bean Validation failure (missing field, bad format) | Jersey (Bean Validation) | 400 | Schema-level — automatic, before the domain is even reached |
| `NullPointerException`, unexpected crash | anywhere | 500 | A bug — not the client's fault |

Two different kinds of errors, two different mechanisms:
- **Schema errors** (missing field, malformed value) are caught by Bean
  Validation annotations on the DTO (`@NotNull`, `@Pattern`, etc.) —
  Jersey handles these automatically and returns 400, *before* a use case
  ever runs. **Pitfall**: without `@Valid` on the resource method's
  parameter, the DTO's annotations are silently ignored.
  ```java
  public class CreateHeroRequest {
      @NotNull public String name;
      @NotNull @Valid public AbilityScoresRequest abilities;
  }
  ```
- **Business rule errors** (invalid opening hours, not enough mana, wrong
  turn) are the domain's own exceptions, caught by an **`ExceptionMapper`**
  in the api layer — the domain doesn't know this mapping exists.

```java
@Provider
public class HeroExceptionMapper
        implements ExceptionMapper<HeroException> {

    @Override
    public Response toResponse(HeroException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.TEXT_PLAIN)
                .entity(exception.getMessage())
                .build();
    }
}
```
A mapper targeting a common parent exception (e.g. `HeroException`) covers
all its subclasses at once; a more specific mapper on a subclass
(`InventoryFullException` → 409) overrides it for just that case — Jersey
picks whichever mapper's type is closest to the thrown exception. Add a
catch-all `ExceptionMapper<Exception>` as a safety net for anything
unexpected, but **let Jersey's own exceptions pass through** (check
`instanceof WebApplicationException` and rethrow its response) — otherwise
an unmapped route (which Jersey itself turns into a 404) gets swallowed
and turned into a 500. Log the real detail server-side; never put a stack
trace, a class name, or a database/internal detail in the response body.

**Mapper pitfalls checklist**: an unregistered mapper is silently ignored
(no error — check it's `register()`-ed, see below); a mapper that lives in
the domain package violates DIP; a `try/catch` repeated in every resource
duplicates what a mapper does once; an overly broad catch-all turns
Jersey's own 404s into 500s; `@Valid` forgotten means the DTO's annotations
are never checked.

**Optional refinement** (not required for v1, worth knowing): a uniform
JSON error body instead of plain text —
```json
{ "error": "INVALID_PARAMETER", "description": "Not enough mana to cast this spell." }
```
— `error` is a stable, machine-readable code; `description` is for humans;
never a stack trace. (The matching standard, for reference only, is
[RFC 9457 Problem Details](https://www.rfc-editor.org/rfc/rfc9457).)

### Jersey basics

JAX-RS (`jakarta.ws.rs.*` — not the older `javax.ws.rs.*` some tutorials
still show) is the *specification*: annotations and interfaces, no running
code. **Jersey** is the implementation that reads those annotations,
receives requests, and calls the methods.

| Annotation | Meaning |
|---|---|
| `@Path("/heroes")` | The resource's path |
| `@GET` `@POST` `@PUT` `@DELETE` | Verb handled by this method |
| `@PathParam("id")` | Value taken from the path |
| `@QueryParam("date")` | Value taken from after the `?` |
| `@HeaderParam("...")` | Value taken from a header |
| `@Valid` | Triggers Bean Validation on the DTO |
| `ExceptionMapper<T>` | Translates an exception into a `Response` |
| `ResourceConfig` | Registers resources and mappers |

A resource stays **thin**: it receives the request, maps the DTO to a
command, calls the use case, maps the output to a response. It does
**not** contain business rules (e.g. validating a value's range belongs to
the domain, not an `if` in the resource method), does **not** talk to a
repository directly (that's the use case's job), and does **not** pass a
request DTO straight into a domain constructor (that would make the domain
depend on the api layer).

```java
@Path("/heroes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HeroResource {
    private final CreateHeroUseCase createHero;

    public HeroResource(CreateHeroUseCase useCase) {
        this.createHero = useCase;
    }

    @POST
    public Response create(@Valid CreateHeroRequest request) {
        CreateHeroCommand command = HeroMapper.toCommand(request);
        HeroOutput output = createHero.execute(command);
        URI location = URI.create("/heroes/" + output.id());
        return Response.created(location).build();
    }
}
```

Request flow: `JSON → DTO (api) → Mapper.toCommand → Command (application)
→ UseCase.execute (domain work happens here) → Output (application) →
Response (api) → JSON`. Each layer only knows its own objects; the DTO and
mapper are the boundary — if the JSON shape changes, only the DTO and
mapper change.

**Choosing a `Response` helper**:

| Method | Code | When |
|---|---|---|
| `Response.ok(body)` | 200 | A read, or an action that returns a result |
| `Response.created(uri)` | 201 | A creation (`POST`) |
| `Response.noContent()` | 204 | A deletion, or an action with no result |
| `Response.status(BAD_REQUEST)` | 400 | Mostly inside an `ExceptionMapper` |

Always end with `.build()`.

**Wiring it together** — an installer is the one place that knows the
concrete classes:
```java
public class HeroInstaller {
    public void install(ResourceConfig config) {
        HeroRepository repository = new InMemoryHeroRepository();
        CreateHeroUseCase createHero = new CreateHeroUseCase(repository);

        config.register(new HeroResource(createHero));
        config.register(new HeroExceptionMapper());
    }
}
```
Without `register(...)`, a resource or mapper is silently ignored — no
error, it just never fires. (Alternative: `@Provider` + Jersey package
scanning — shorter, but less visible what's actually wired up.)

### Pagination

Never return an unbounded list (e.g. every event in the event log, or
every hero a prolific player has created). Page via query parameters —
they're options on a collection, not identity:

```
GET /heroes/{id}/events?page=0&size=20
```
```json
{
  "items": [ /* ... */ ],
  "page": 0,
  "size": 20,
  "totalItems": 134,
  "totalPages": 7
}
```
Pick and document a default size (e.g. 20) and a max (e.g. 100), and
whether page numbering starts at 0 or 1. **Pitfall**: loading the whole
collection and slicing it in the resource defeats the purpose — the page
should be requested from the repository (`findByX(id, pageQuery)`), not
cut after the fact.

### HATEOAS (good to know, not required for v1)

The Richardson Maturity Model: level 0 (one endpoint, action in the body —
RPC over HTTP), level 1 (resources have their own URIs), level 2 (proper
verbs + status codes — **this is where this project's API should sit**),
level 3 (HATEOAS: the response includes links to the actions currently
available on that resource, so the client doesn't have to guess — e.g. no
`attack` link on a combat that's already over). Level 3 is worth
understanding conceptually; not expected to be built for v1 unless the
team chooses it as one of the three original User Stories.

### Common mistakes to avoid

A verb in the URI (`/getHeroes`); a `GET` that mutates something; `200`
for every response including errors; a stack trace or internal class name
reaching the client; business rules living in the resource instead of the
domain; a request DTO passed straight into a domain constructor; an
unregistered resource/mapper silently doing nothing; a catch-all exception
mapper that also swallows Jersey's own 404s; `@Valid` forgotten so Bean
Validation never runs.