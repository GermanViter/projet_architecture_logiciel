package com.rpg.domain.hero;

import com.rpg.domain.hero.exceptions.DuplicateHeroNameException;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static com.rpg.domain.hero.fixtures.HeroFixture.HERO_NAME;
import static com.rpg.domain.hero.fixtures.HeroFixture.UNKNOWN_HERO_NAME;
import static com.rpg.domain.hero.fixtures.HeroFixture.aHero;
import static com.rpg.domain.hero.fixtures.HeroFixture.anotherHero;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HeroRegistryTest {
    private final HeroRegistry registry = new HeroRegistry();

    @Test
    void findByName_registeredHero_isFound() {
        Hero hero = aHero();
        registry.addHero(hero);
        registry.addHero(anotherHero());

        assertThat(registry.findByName(new HeroName(HERO_NAME))).hasValue(hero);
    }

    @Test
    void findByName_unknownName_findsNothing() {
        registry.addHero(aHero());

        assertThat(registry.findByName(new HeroName(UNKNOWN_HERO_NAME))).isEmpty();
    }

    @Test
    void addHero_nameAlreadyRegistered_isRefused() {
        registry.addHero(aHero());

        assertThrows(DuplicateHeroNameException.class, () -> registry.addHero(aHero()));
    }
}
