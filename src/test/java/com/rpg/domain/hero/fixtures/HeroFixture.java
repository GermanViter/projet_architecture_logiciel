package com.rpg.domain.hero.fixtures;

import com.rpg.domain.hero.Hero;
import com.rpg.domain.hero.HeroName;
import com.rpg.domain.hero.characteristics.Characteristic;
import com.rpg.domain.hero.characteristics.Characteristics;
import com.rpg.domain.hero.role.CharacterRole;
import com.rpg.domain.hero.role.Warrior;
import com.rpg.domain.item.Inventory;

import java.util.List;

public final class HeroFixture {
    public static final String VALID_HERO_NAME = "Gandalf";
    public static final String OTHER_HERO_NAME = "Aragorn";

    private HeroFixture() {}

    public static Hero aHero() {
        return new Hero(
                new HeroName(VALID_HERO_NAME),
                List.of(
                        Characteristic.create(Characteristics.STRENGTH, 10),
                        Characteristic.create(Characteristics.DEXTERITY, 10),
                        Characteristic.create(Characteristics.CONSTITUTION, 10)
                ),
                new Warrior(),
                new Inventory()
        );
    }

    public static Hero aHero(String name) {
        return new Hero(
                new HeroName(name),
                List.of(
                        Characteristic.create(Characteristics.STRENGTH, 10),
                        Characteristic.create(Characteristics.DEXTERITY, 10),
                        Characteristic.create(Characteristics.CONSTITUTION, 10)
                ),
                new Warrior(),
                new Inventory()
        );
    }

    public static Hero anotherHero() {
        return aHero(OTHER_HERO_NAME);
    }
}
