package com.rpg.domain.hero;

import com.rpg.domain.hero.characteristics.Characteristic;
import com.rpg.domain.hero.role.CharacterRole;
import com.rpg.domain.hero.species.Species;
import com.rpg.domain.item.Inventory;

import java.util.ArrayList;
import java.util.List;

public final class HeroFactory {

    public Hero create(HeroName name, Species species, CharacterRole role, List<Characteristic> chosenCharacteristics) {
        return new Hero(name, applySpeciesBonuses(species, chosenCharacteristics), role, new Inventory());
    }

    private ArrayList<Characteristic> applySpeciesBonuses(Species species, List<Characteristic> chosenCharacteristics) {
        ArrayList<Characteristic> finalCharacteristics = new ArrayList<>();
        for (Characteristic characteristic : chosenCharacteristics) {
            finalCharacteristics.add(species.applyBonusTo(characteristic));
        }
        return finalCharacteristics;
    }
}
