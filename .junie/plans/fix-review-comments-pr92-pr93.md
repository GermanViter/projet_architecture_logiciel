---
sessionId: session-261116-230609-12yw
---

# État actuel du code

## PR #93 (80-l6-lancer-la-partie) — Fichiers existants

### `Lobby.java`
- `MIN_PLAYERS` et `MAX_PLAYERS` en `static final` (package-private) au lieu de `private`.
- `startGame(Pseudonym)` vérifie `playerCount < MIN_PLAYERS` (code mort : le lobby contient toujours son hôte) et `playerCount > MAX_PLAYERS` (code mort : `join()` bloque à 4). La branche `> MAX_PLAYERS` lève `NotEnoughPlayersException` au lieu de `TooManyPlayersException`.
- `TooManyPlayersException` créée mais jamais utilisée.
- `join()` et `leave()` ne vérifient pas `started` → un joueur peut rejoindre/quitter après le lancement.

### `LobbyResource.java`
- `GAME_PATH` séparé des autres constantes (`LOBBY_PLAYERS_PATH`, `PLAYER_ID_PATH`).

### `StartGameUseCaseTest.java`
- Deux tests redondants : `execute_hostStartsGame_marksLobbyAsStarted` et `execute_hostStartsGame_returnsUpdatedLobby`.
- Pas de tests pour `join()`/`leave()` après lancement.
- Pas de tests `LobbyTest` pour `startGame()`.

### `JoinLobbyUseCaseTest.java`
- `false` en dur pour le champ `started` au lieu d'une constante nommée.

### Mappers d'exceptions
- Pas de `NotHostExceptionMapper` ni `GameAlreadyStartedExceptionMapper` → les erreurs tombent dans `LobbyExceptionMapper` (409 Conflict pour tout).

---

## PR #92 (79-l5-sélectionner-un-héros) — Code absent

La fonctionnalité de sélection de héros n'existe pas dans le code actuel. Elle doit être créée en tenant compte des corrections de la review :
- `playerId` du chemin doit être vérifié (comme `LeaveLobbyUseCase.ensureLeavesThemselves`).
- `HeroRegistry.findByName(HeroName)` au lieu de parcourir `allHeroes()`.
- `HeroNotFoundException` + mapper (404) au lieu de `IllegalArgumentException`.
- `leave()` doit libérer le héros sélectionné.
- `HeroRegistry` pré-rempli dans `LobbyInstaller`.
- Pas de code mort (`availableHeroes`, etc.).
- `String` nullable pour `selectedHero` dans `LobbyMemberOutput`, pas `Optional`.

# Delivery Steps

### ✓ Step 1: PR #93 - Corriger Lobby.startGame() et fixation du groupe
Nettoyer le code mort dans `Lobby.java` et bloquer `join()`/`leave()` après le lancement.

- Supprimer les vérifications `MIN_PLAYERS` et `MAX_PLAYERS` dans `startGame()` (code mort car le lobby contient toujours son hôte et `join()` bloque à 4).
- Supprimer `TooManyPlayersException` (jamais utilisée) et `NotEnoughPlayersException` (jamais levée depuis startGame).
- Remettre `MIN_PLAYERS` et `MAX_PLAYERS` en `private` dans `Lobby`.
- Bloquer `join()` si `started == true` (lancer une `GameAlreadyStartedException`).
- Bloquer `leave()` si `started == true` (lancer une `GameAlreadyStartedException`).
- Extraire `int playerCount = members.size()` dans une méthode nommée `ensureGroupIsPlayable()` (même si les vérifications sont supprimées, pour clarté).

Fichiers affectés : `Lobby.java`, suppression de `TooManyPlayersException.java` et `NotEnoughPlayersException.java`.

### ✓ Step 2: PR #93 - Tests Lobby.startGame() et fixation du groupe
Ajouter les tests manquants dans `LobbyTest.java` et fusionner les tests redondants dans `StartGameUseCaseTest.java`.

- Dans `LobbyTest` : ajouter `startGame_hostStartsGame_marksAsStarted`, `startGame_gameAlreadyStarted_isRefused`, `startGame_nonHost_isRefused`, `join_afterGameStarted_isRefused`, `leave_afterGameStarted_isRefused`.
- Dans `StartGameUseCaseTest` : fusionner `execute_hostStartsGame_marksLobbyAsStarted` et `execute_hostStartsGame_returnsUpdatedLobby` en un seul test.
- Ajouter un test `join_afterGameStarted_isRefused` et `leave_afterGameStarted_isRefused` au niveau use case si pertinent.
- Remplacer les `false` en dur dans `JoinLobbyUseCaseTest` par une constante nommée (ex: `LOBBY_NOT_STARTED = false`).

Fichiers affectés : `LobbyTest.java`, `StartGameUseCaseTest.java`, `JoinLobbyUseCaseTest.java`, `CreateLobbyUseCaseTest.java`.

### ✓ Step 3: PR #93 - Nettoyage API et mappers d'exceptions
Regrouper les constantes dans `LobbyResource` et ajouter les mappers d'exceptions manquants.

- Regrouper `GAME_PATH` avec les autres constantes de chemin dans `LobbyResource`.
- Créer `NotHostExceptionMapper` (403 Forbidden) pour retourner une erreur HTTP appropriée.
- Créer `GameAlreadyStartedExceptionMapper` (409 Conflict) pour retourner une erreur HTTP appropriée.
- Enregistrer les nouveaux mappers dans `LobbyInstaller`.

Fichiers affectés : `LobbyResource.java`, `LobbyInstaller.java`, création de `NotHostExceptionMapper.java` et `GameAlreadyStartedExceptionMapper.java`.

### ✓ Step 4: PR #92 - Implémenter la sélection de héros (corrections review)
Créer la fonctionnalité de sélection de héros en tenant compte des corrections de la review.

- Créer `SelectHeroUseCase` + `SelectHeroCommand` avec vérification que le `playerId` du chemin correspond au joueur du token (comme `LeaveLobbyUseCase.ensureLeavesThemselves`).
- Ajouter dans `Lobby` : map `selectedHeroes` (player → hero), `selectHero()`, `clearHeroSelection()` appelé dans `leave()`.
- Ajouter `findByName(HeroName)` dans `HeroRegistry` au lieu de parcourir `allHeroes()` dans le use case.
- Créer `HeroNotFoundException` + mapper (404) au lieu de `IllegalArgumentException`.
- Créer `HeroAlreadySelectedException`, `HeroCannotSelectOwnHeroOnly` (héros propre = le joueur ne peut sélectionner que son propre héros, pas celui d'un autre).
- Ajouter `selectedHero` dans `LobbyMemberOutput` (sans `Optional`, utiliser `String` nullable).
- Route `PUT /lobbies/{lobbyId}/players/{playerId}/hero` dans `LobbyResource`.
- Pré-remplir `HeroRegistry` dans `LobbyInstaller` avec des héros (via `HeroFactory`).

Fichiers affectés : `Lobby.java`, `HeroRegistry.java`, `LobbyMemberOutput.java`, `LobbyResource.java`, `LobbyInstaller.java`, et création de nouveaux fichiers pour le use case, les exceptions, et les mappers.

### ✓ Step 5: PR #92 - Tests de sélection de héros
Créer les tests pour la sélection de héros.

- `SelectHeroUseCaseTest` : cas nominal, playerId ne correspond pas au token (équivalent de `CannotRemoveAnotherPlayerException`), héros déjà sélectionné, héros déjà pris, jeu déjà démarré, lobby inconnu, token inconnu, héros introuvable.
- `LobbyTest` : `selectHero_playerSelectsHero_hasSelectedHero`, `selectHero_heroAlreadyTaken_isRefused`, `selectHero_playerAlreadySelected_isRefused`, `leave_playerLeaves_clearsHeroSelection`.
- Tests de resource/mapper pour la route PUT.

Fichiers affectés : création de `SelectHeroUseCaseTest.java`, mise à jour de `LobbyTest.java`.