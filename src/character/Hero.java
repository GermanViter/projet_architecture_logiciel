package character;

import character.characteristics.Characteristic;
import character.characteristics.Characteristics;
import dice.DiceRoller;
import item.Weapon;
import item.Armor;

import java.util.ArrayList;
import java.util.Optional;

public final class Hero {
    private static final int STARTING_LEVEL = 1;
    private static final int STARTING_EXPERIENCE = 0;
    private static final int UNARMED_DAMAGE = 1;

    private final HeroName name;
    private final ArrayList<Characteristic> characteristics;
    private int level;
    private int experience;
    private Weapon equippedWeapon;
    private Armor equippedArmor;

    public Hero(HeroName name, ArrayList<Characteristic> characteristics) {
        this.name = name;
        this.characteristics = characteristics != null ? new ArrayList<>(characteristics) : new ArrayList<>();
        this.level = STARTING_LEVEL;
        this.experience = STARTING_EXPERIENCE;
        this.equippedWeapon = null;
        this.equippedArmor = null;
    }

    public void equip(Weapon weapon) {
        this.equippedWeapon = weapon;
    }

    public void equip(Armor armor) {
        this.equippedArmor = armor;
    }

    public int attack(DiceRoller roller) {
        if (equippedWeapon == null) {
            return UNARMED_DAMAGE;
        }
        return equippedWeapon.getDamage().roll(roller);
    }

    public HeroName name() {
        return name;
    }

    public ArrayList<Characteristic> characteristics() {
        return new ArrayList<>(characteristics);
    }

    public Optional<Characteristic> getCharacteristic(Characteristics type) {
        return characteristics.stream()
                .filter(c -> c.type() == type)
                .findFirst();
    }

    public int level() {
        return level;
    }

    public int experience() {
        return experience;
    }
}
