package com.rpg.domain.hero.role;

import com.rpg.domain.hero.characteristics.Characteristics;
import com.rpg.domain.item.Armor;
import com.rpg.domain.item.ArmorCategory;
import com.rpg.domain.item.Item;
import com.rpg.domain.item.ItemName;
import com.rpg.domain.item.Weapon;
import com.rpg.domain.item.WeaponCategory;

import java.util.List;

public class Warrior implements CharacterRole {

    @Override
    public int baseHp() {
        return 12;
    }

    @Override
    public int baseMana() {
        return 0;
    }

    @Override
    public Characteristics mainCharacteristic() {
        return Characteristics.STRENGTH;
    }

    @Override
    public boolean canEquip(WeaponCategory category) {
        return category == WeaponCategory.SWORD
                || category == WeaponCategory.AXE
                || category == WeaponCategory.MACE
                || category == WeaponCategory.STAFF
                || category == WeaponCategory.DAGGER;
    }

    @Override
    public boolean canEquip(ArmorCategory category) {
        return true;
    }

    @Override
    public List<Item> initialEquipment() {
        return List.of(
            new Weapon(new ItemName("Long Sword"), WeaponCategory.SWORD),
            new Armor(new ItemName("Mesh Armor"), ArmorCategory.CHAINMAIL)
        );
    }
}
