package com.rpg.domain.hero.role;

import com.rpg.domain.hero.characteristics.Characteristics;
import com.rpg.domain.item.ArmorCategory;
import com.rpg.domain.item.Item;
import com.rpg.domain.item.WeaponCategory;

import java.util.List;

public interface CharacterRole {
    int baseHp();
    int baseMana();
    Characteristics mainCharacteristic();
    boolean canEquip(WeaponCategory category);
    boolean canEquip(ArmorCategory category);
    List<Item> initialEquipment();
}
