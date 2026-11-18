package com.rpg.domain.hero;

import com.rpg.domain.hero.exceptions.DuplicateHeroNameException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HeroRegistry implements HeroRepository {
    private final List<Hero> heroes = new ArrayList<>();

    public void addHero(Hero hero) {
        if (nameAlreadyExists(hero.name())) {
            throw new DuplicateHeroNameException(hero.name());
        }

        heroes.add(hero);
    }

    private boolean nameAlreadyExists(HeroName name) {
        return findByName(name).isPresent();
    }

    public List<Hero> allHeroes() {
        return new ArrayList<>(heroes);
    }

    @Override
    public Optional<Hero> findByName(HeroName name) {
        return heroes.stream()
                .filter(hero -> hero.name().equals(name))
                .findFirst();
    }
}
