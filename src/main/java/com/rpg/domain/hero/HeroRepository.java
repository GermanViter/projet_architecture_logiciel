package com.rpg.domain.hero;

import java.util.Optional;

public interface HeroRepository {
    Optional<Hero> findByName(HeroName name);
}
