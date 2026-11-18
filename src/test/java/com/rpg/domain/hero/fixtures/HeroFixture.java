package com.rpg.domain.hero.fixtures;

import com.rpg.domain.hero.Hero;
import com.rpg.domain.hero.HeroName;
import com.rpg.domain.hero.characteristics.Characteristic;
import com.rpg.domain.hero.characteristics.Characteristics;
import com.rpg.domain.hero.role.Warrior;
import com.rpg.domain.item.Inventory;

import java.util.Arrays;
import java.util.List;

public final class HeroFixture {
    public static final String HERO_NAME = "Aldric";
    public static final String OTHER_HERO_NAME = "Elowen";
    public static final String UNKNOWN_HERO_NAME = "Nobody";
    public static final List<String> DISTINCT_HERO_NAMES =
            List.of(HERO_NAME, OTHER_HERO_NAME, "Brunhild", "Grukk");
    public static final int AVERAGE_SCORE = 10;

    private HeroFixture() {}

    public static Hero aHero() {
        return aHeroNamed(HERO_NAME);
    }

    public static Hero anotherHero() {
        return aHeroNamed(OTHER_HERO_NAME);
    }

    public static Hero aHeroNamed(String name) {
        return new Hero(new HeroName(name), averageCharacteristics(), new Warrior(), new Inventory());
    }

    private static List<Characteristic> averageCharacteristics() {
        return Arrays.stream(Characteristics.values())
                .map(type -> new Characteristic(type, AVERAGE_SCORE))
                .toList();
    }
}
