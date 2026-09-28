package item;

import character.characteristics.Characteristics;
import dice.Dice;

import java.util.Objects;

public class Weapon implements Item {

    private final String name;
    private final Dice damage;
    private final Characteristics characteristic;
    private final WeaponCategory category;

    public Weapon(String name, Dice damage, Characteristics characteristic, WeaponCategory category) {
        this.name = Objects.requireNonNull(name, "Weapon name cannot be null.");
        this.damage = Objects.requireNonNull(damage, "Weapon damage cannot be null.");
        this.characteristic = Objects.requireNonNull(characteristic, "Weapon characteristic cannot be null.");
        this.category = Objects.requireNonNull(category, "Weapon category cannot be null.");
    }

    public static Weapon longSword() {
        return new Weapon("Longsword", new Dice(1, 8, 0), Characteristics.STRENGTH, WeaponCategory.SWORD);
    }

    public static Weapon axe() {
        return new Weapon("Axe", new Dice(1, 10, 0), Characteristics.STRENGTH, WeaponCategory.AXE);
    }

    public static Weapon mace() {
        return new Weapon("Mace", new Dice(1, 6, 0), Characteristics.STRENGTH, WeaponCategory.MACE);
    }

    public static Weapon dagger() {
        return new Weapon("Dagger", new Dice(1, 4, 0), Characteristics.DEXTERITY, WeaponCategory.DAGGER);
    }

    public static Weapon bow() {
        return new Weapon("Bow", new Dice(1, 8, 0), Characteristics.DEXTERITY, WeaponCategory.BOW);
    }

    public static Weapon staff() {
        return new Weapon("Staff", new Dice(1, 6, 0), Characteristics.STRENGTH, WeaponCategory.STAFF);
    }

    @Override
    public String getName() {
        return name;
    }

    public Dice getDamage() {
        return damage;
    }

    public Characteristics getCharacteristic() {
        return characteristic;
    }

    public WeaponCategory getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return name + " (" + damage + ", " + characteristic + ")";
    }
}