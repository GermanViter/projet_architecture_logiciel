package com.rpg.domain.hero.species;

import com.rpg.domain.hero.characteristics.Characteristic;
import com.rpg.domain.hero.characteristics.Characteristics;

import java.util.Map;

import static com.rpg.domain.hero.characteristics.Characteristics.CHARISMA;
import static com.rpg.domain.hero.characteristics.Characteristics.CONSTITUTION;
import static com.rpg.domain.hero.characteristics.Characteristics.DEXTERITY;
import static com.rpg.domain.hero.characteristics.Characteristics.INTELLIGENCE;
import static com.rpg.domain.hero.characteristics.Characteristics.STRENGTH;
import static com.rpg.domain.hero.characteristics.Characteristics.WISDOM;

public enum Species {
    HUMAN(Map.of(
            STRENGTH, 1,
            DEXTERITY, 1,
            CONSTITUTION, 1,
            INTELLIGENCE, 1,
            WISDOM, 1,
            CHARISMA, 1)),
    ELF(Map.of(
            DEXTERITY, 2,
            INTELLIGENCE, 1)),
    DWARF(Map.of(
            CONSTITUTION, 2,
            STRENGTH, 1)),
    ORC(Map.of(
            STRENGTH, 2,
            CONSTITUTION, 1,
            INTELLIGENCE, -1));

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
