package com.rpg.domain.hero.role;

import com.rpg.domain.hero.characteristics.Characteristics;
import com.rpg.domain.item.Armor;
import com.rpg.domain.item.ArmorCategory;
import com.rpg.domain.item.Item;
import com.rpg.domain.item.ItemName;
import com.rpg.domain.item.Weapon;
import com.rpg.domain.item.WeaponCategory;

import java.util.List;

public class Mage implements CharacterRole {

    @Override
    public int baseHp() {
        return 6;
    }

    @Override
    public int baseMana() {
        return 10;
    }

    @Override
    public Characteristics mainCharacteristic() {
        return Characteristics.INTELLIGENCE;
    }

    @Override
    public boolean canEquip(WeaponCategory category) {
        return category == WeaponCategory.STAFF || category == WeaponCategory.DAGGER;
    }

    @Override
    public boolean canEquip(ArmorCategory category) {
        return category == ArmorCategory.NONE;
    }

    @Override
    public List<Item> initialEquipment() {
        return List.of(
            new Weapon(new ItemName("Staff"), WeaponCategory.STAFF)
        );
    }
}
