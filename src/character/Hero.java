package character;

import character.characteristics.Characteristic;
import character.characteristics.Characteristics;
import character.role.CharacterRole;
import dice.DiceRoller;
import item.Armor;
import item.Equipable;
import item.Inventory;
import item.ItemNotOwnedException;
import item.Weapon;

import java.util.ArrayList;
import java.util.Optional;

public final class Hero {
    private static final int STARTING_LEVEL = 1;
    private static final int STARTING_EXPERIENCE = 0;
    private static final int UNARMED_DAMAGE = 1;

    private final HeroName name;
    private final ArrayList<Characteristic> characteristics;
    private final Inventory inventory;
    private final CharacterRole role;
    private int level;
    private int experience;
    private Weapon equippedWeapon;
    private Armor equippedArmor;

    public Hero(HeroName name, ArrayList<Characteristic> characteristics, CharacterRole role, Inventory inventory) {
        this.name = name;
        this.characteristics = characteristics != null ? new ArrayList<>(characteristics) : new ArrayList<>();
        this.role = role;
        this.inventory = inventory;
        this.level = STARTING_LEVEL;
        this.experience = STARTING_EXPERIENCE;
        this.equippedWeapon = null;
        this.equippedArmor = null;
    }

    public void equip(Equipable item) {
        if (!inventory.contains(item)) {
            throw new ItemNotOwnedException(item);
        }

        if (!item.canBeEquippedBy(role)) {
            throw new ClassRestrictionException(item.getName(), role);
        }

        if (item instanceof Weapon weapon) {
            if (equippedWeapon != null) {
                inventory.addItem(equippedWeapon);
            }
            inventory.removeItem(item);
            this.equippedWeapon = weapon;
        } else if (item instanceof Armor armor) {
            if (equippedArmor != null) {
                inventory.addItem(equippedArmor);
            }
            inventory.removeItem(item);
            this.equippedArmor = armor;
        }
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

    public CharacterRole role() {
        return role;
    }
}
