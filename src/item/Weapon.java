package item;

import character.characteristics.Characteristics;
import character.role.CharacterRole;
import dice.Dice;

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