package com.rpg.domain.hero.species;

import com.rpg.domain.hero.characteristics.Characteristic;
import com.rpg.domain.hero.characteristics.Characteristics;

import java.util.Map;

public enum Species {
    ;

    private static final int NO_BONUS = 0;

    private final Map<Characteristics, Integer> bonuses;

    Species(Map<Characteristics, Integer> bonuses) {
        this.bonuses = bonuses;
    }

    public Characteristic applyBonusTo(Characteristic characteristic) {
        return characteristic.applyBonus(bonusGrantedTo(characteristic.type()));
    }

    private int bonusGrantedTo(Characteristics type) {
        return bonuses.getOrDefault(type, NO_BONUS);
    }
}
