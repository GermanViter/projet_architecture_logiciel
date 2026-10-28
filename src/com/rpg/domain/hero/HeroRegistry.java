package com.rpg.domain.hero;

import com.rpg.domain.hero.exceptions.DuplicateHeroNameException;
import java.util.ArrayList;
import java.util.List;

public class HeroRegistry {

    private final List<Hero> heroes = new ArrayList<>();

    public void addHero(Hero hero) {
        if (nameAlreadyExists(hero.name())) {
            throw new DuplicateHeroNameException(hero.name());
        }

        heroes.add(hero);
    }

    private boolean nameAlreadyExists(HeroName name) {
        for (Hero existingHero : heroes) {
            if (existingHero.name().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public List<Hero> allHeroes() {
        return new ArrayList<>(heroes);
    }
}
