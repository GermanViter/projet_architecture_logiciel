package com.rpg.domain.item;
import com.rpg.domain.item.exceptions.*;
import com.rpg.domain.hero.characteristics.Characteristics;
import com.rpg.domain.hero.role.CharacterRole;
import com.rpg.domain.dice.Dice;
import java.util.Objects;




public class Weapon implements Equipable {

    private final ItemName name;
    private final WeaponCategory category;

    public Weapon(ItemName name, WeaponCategory category) {
        this.name = name;
        this.category = Objects.requireNonNull(category, "Weapon category cannot be null.");
    }

    @Override
    public String getName() {
        return name.value();
    }

    public WeaponCategory getCategory() {
        return category;
    }

    public Dice getDamage() {
        return category.damage();
    }

    public Characteristics getCharacteristic() {
        return category.characteristic();
    }

    @Override
    public boolean canBeEquippedBy(CharacterRole role) {
        return category.canBeEquippedBy(role);
    }

    @Override
    public String toString() {
        return name + " (" + getDamage() + ", " + getCharacteristic() + ")";
    }
}
