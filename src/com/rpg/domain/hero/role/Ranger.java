package com.rpg.domain.hero.role;

import com.rpg.domain.hero.characteristics.Characteristics;
import com.rpg.domain.item.Armor;
import com.rpg.domain.item.ArmorCategory;
import com.rpg.domain.item.Item;
import com.rpg.domain.item.ItemName;
import com.rpg.domain.item.Weapon;
import com.rpg.domain.item.WeaponCategory;

import java.util.List;

public class Ranger implements CharacterRole {

    @Override
    public int baseHp() {
        return 10;
    }

    @Override
    public int baseMana() {
        return 0;
    }

    @Override
    public Characteristics mainCharacteristic() {
        return Characteristics.DEXTERITY;
    }

    @Override
    public boolean canEquip(WeaponCategory category) {
        return true;
    }

    @Override
    public boolean canEquip(ArmorCategory category) {
        return category == ArmorCategory.NONE || category == ArmorCategory.LEATHER;
    }

    @Override
    public List<Item> initialEquipment() {
        return List.of(
            new Weapon(new ItemName("Bow"), WeaponCategory.BOW),
            new Armor(new ItemName("Leather Armor"), ArmorCategory.LEATHER)
        );
    }
}
