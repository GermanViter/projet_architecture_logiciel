package com.rpg.domain.item;

import com.rpg.domain.hero.characteristics.Characteristics;
import com.rpg.domain.dice.Dice;

public enum WeaponCategory {
    SWORD(new Dice(1, 8, 0), Characteristics.STRENGTH),
    AXE(new Dice(1, 10, 0), Characteristics.STRENGTH),
    MACE(new Dice(1, 6, 0), Characteristics.STRENGTH),
    DAGGER(new Dice(1, 4, 0), Characteristics.DEXTERITY),
    BOW(new Dice(1, 8, 0), Characteristics.DEXTERITY),
    STAFF(new Dice(1, 6, 0), Characteristics.STRENGTH);

    private final Dice damage;
    private final Characteristics characteristic;

    WeaponCategory(Dice damage, Characteristics characteristic) {
        this.damage = damage;
        this.characteristic = characteristic;
    }

    public Dice damage() {
        return damage;
    }

    public Characteristics characteristic() {
        return characteristic;
    }
}
