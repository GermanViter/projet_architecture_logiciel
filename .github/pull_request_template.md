<!--
Titre de la PR : Conventional Commits en français, ex. « feat(lobby): ajoute la sélection de héros ».
Branche cible : develop, sauf indication contraire.
Supprime les sections qui ne s'appliquent pas (ex. « Comment tester » pour un changement de documentation, « Issue liée » si la PR ne ferme aucune issue).
-->

## Résumé
<!-- Une ou deux phrases : ce que fait la PR et pourquoi. -->

## Changements
<!-- Une puce par changement significatif, regroupées par classe ou package. Mentionne les classes ajoutées, renommées ou supprimées. -->
-

## Comment tester
<!-- Étapes qu'un coéquipier peut suivre : lancer le serveur, appeler une route, lancer `mvn test`... -->
1.

## Issue liée
Closes #

## Notes pour les relecteurs
<!-- Doutes, compromis faits, ou ce qui a été volontairement laissé de côté. -->

## Liste de vérification

**Avant d'ouvrir la PR**
- [ ] Le code compile
- [ ] La branche est rebasée sur `develop` à jour
- [ ] Les commits suivent Conventional Commits, en français
- [ ] La PR porte sur un seul sujet (idéalement moins de ~400 lignes modifiées)

**Code propre**
- [ ] Les noms révèlent l'intention et utilisent le vocabulaire du domaine
- [ ] Chaque méthode fait une seule chose
- [ ] Aucune duplication de règle (DRY)
- [ ] Aucun nombre ni chaîne magique, aucun commentaire, aucun `System.out.println` de débogage
- [ ] Aucun setter : l'état change par des actions du domaine (Tell, Don't Ask, loi de Déméter)
- [ ] Les erreurs sont des exceptions du domaine, pas des exceptions génériques

**Tests (nouveau code du domaine)**
- [ ] Chaque test a un Given, un When et un Then visibles
- [ ] Chaque limite a un test juste en dessous et un test à la limite
- [ ] Chaque objet du domaine utilisé a une fixture, sans valeurs brutes dans les tests
- [ ] Aucun `now()` ni hasard réel ; les ports sont mockés
- [ ] Chaque critère d'acceptation de l'issue a au moins un test
- [ ] `mvn test` passe
