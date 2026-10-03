package com.rpg.domain.hero.role;

import com.rpg.domain.hero.characteristics.Characteristics;
import com.rpg.domain.item.Armor;
import com.rpg.domain.item.ArmorCategory;
import com.rpg.domain.item.Item;
import com.rpg.domain.item.ItemName;
import com.rpg.domain.item.Weapon;
import com.rpg.domain.item.WeaponCategory;

import java.util.List;

public class Cleric implements CharacterRole {

    @Override
    public int baseHp() {
        return 10;
    }

    @Override
    public int baseMana() {
        return 8;
    }

    @Override
    public Characteristics mainCharacteristic() {
        return Characteristics.WISDOM;
    }

    @Override
    public boolean canEquip(WeaponCategory category) {
        return category == WeaponCategory.MACE || category == WeaponCategory.SWORD;
    }

    @Override
    public boolean canEquip(ArmorCategory category) {
        return category == ArmorCategory.LEATHER || category == ArmorCategory.CHAINMAIL;
    }

    @Override
    public List<Item> initialEquipment() {
        return List.of(
            new Weapon(new ItemName("Mace"), WeaponCategory.MACE),
            new Armor(new ItemName("Chainmail Armour"), ArmorCategory.CHAINMAIL)
        );
    }
}
