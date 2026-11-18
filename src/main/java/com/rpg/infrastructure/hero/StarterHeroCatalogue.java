package com.rpg.infrastructure.hero;

import com.rpg.domain.hero.Hero;
import com.rpg.domain.hero.HeroFactory;
import com.rpg.domain.hero.HeroName;
import com.rpg.domain.hero.HeroRegistry;
import com.rpg.domain.hero.HeroRepository;
import com.rpg.domain.hero.characteristics.Characteristic;
import com.rpg.domain.hero.characteristics.Characteristics;
import com.rpg.domain.hero.role.CharacterRole;
import com.rpg.domain.hero.role.Cleric;
import com.rpg.domain.hero.role.Mage;
import com.rpg.domain.hero.role.Ranger;
import com.rpg.domain.hero.role.Warrior;
import com.rpg.domain.hero.species.Species;

import java.util.Arrays;
import java.util.List;

public final class StarterHeroCatalogue {
    private static final int STARTING_SCORE = 10;
    private static final HeroFactory HERO_FACTORY = new HeroFactory();

    private StarterHeroCatalogue() {}

    public static HeroRepository create() {
        HeroRegistry catalogue = new HeroRegistry();
        catalogue.addHero(starterHero("Aldric", Species.HUMAN, new Warrior()));
        catalogue.addHero(starterHero("Elowen", Species.ELF, new Ranger()));
        catalogue.addHero(starterHero("Brunhild", Species.DWARF, new Cleric()));
        catalogue.addHero(starterHero("Grukk", Species.ORC, new Mage()));
        return catalogue;
    }

    private static Hero starterHero(String name, Species species, CharacterRole role) {
        return HERO_FACTORY.create(new HeroName(name), species, role, startingCharacteristics());
    }

    private static List<Characteristic> startingCharacteristics() {
        return Arrays.stream(Characteristics.values())
                .map(type -> new Characteristic(type, STARTING_SCORE))
                .toList();
    }
}
