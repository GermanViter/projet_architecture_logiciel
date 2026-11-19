package com.rpg.domain.hero.exceptions;

import com.rpg.domain.hero.HeroName;

public class HeroNotFoundException extends RuntimeException {
    public HeroNotFoundException(HeroName name) {
        super("No hero is named \"" + name.getName() + "\".");
    }
}
