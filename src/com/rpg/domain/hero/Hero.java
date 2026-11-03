package com.rpg.domain.hero;
import com.rpg.domain.hero.exceptions.*;
import com.rpg.domain.item.exceptions.*;
import java.util.*;
import com.rpg.domain.hero.characteristics.Characteristic;
import com.rpg.domain.hero.characteristics.Characteristics;
import com.rpg.domain.hero.role.CharacterRole;
import com.rpg.domain.dice.DiceRoller;
import com.rpg.domain.item.Armor;
import com.rpg.domain.item.Equipable;
import com.rpg.domain.item.Inventory;
import com.rpg.domain.item.Item;
import com.rpg.domain.item.Potion;
import com.rpg.domain.item.Usable;
import com.rpg.domain.item.exceptions.ItemNotOwnedException;
import com.rpg.domain.item.Weapon;
import java.util.ArrayList;
import java.util.Optional;






public final class Hero {
    private static final int STARTING_LEVEL = 1;
    private static final int STARTING_EXPERIENCE = 0;
    private static final int UNARMED_DAMAGE = 1;

    private final HeroName name;
    private final List<Characteristic> characteristics;
    private final Inventory inventory;
    private final CharacterRole role;
    private int level;
    private int experience;
    private int currentHp;
    private Weapon equippedWeapon;
    private Armor equippedArmor;

    public Hero(HeroName name, List<Characteristic> characteristics, CharacterRole role, Inventory inventory) {
        this.name = name;
        this.characteristics = characteristics != null ? new ArrayList<>(characteristics) : new ArrayList<>();
        this.role = role;
        this.inventory = inventory;
        this.level = STARTING_LEVEL;
        this.experience = STARTING_EXPERIENCE;
        this.equippedWeapon = null;
        this.equippedArmor = null;
        this.currentHp = maxHp();
        for (Item item : role.initialEquipment()) {
            if (!inventory.isFull()) {
                addItem(item);
            }
        }
    }

    public void equip(Equipable item) {
        if (!hasItem(item)) {
            throw new ItemNotOwnedException(item);
        }

        if (!item.canBeEquippedBy(role)) {
            throw new ClassRestrictionException(item.getName(), role);
        }

        item.equip(this);
    }

    public void equipWeapon(Weapon weapon) {
        if (equippedWeapon != null) {
            addItem(equippedWeapon);
        }
        removeItem(weapon);
        this.equippedWeapon = weapon;
    }

    public void equipArmor(Armor armor) {
        if (equippedArmor != null) {
            addItem(equippedArmor);
        }
        removeItem(armor);
        this.equippedArmor = armor;
    }

    public void use(Usable item, DiceRoller roller) {
        if (!hasItem(item)) {
            throw new ItemNotOwnedException(item);
        }

        if (item instanceof Potion potion) {
            int healedAmount = potion.getHealing().roll(roller);
            heal(healedAmount);
            removeItem(item);
        } else {
            item.use();
        }
    }

    public void takeDamage(int amount) {
        this.currentHp = Math.max(0, this.currentHp - amount);
    }

    public void heal(int amount) {
        this.currentHp = Math.min(maxHp(), this.currentHp + amount);
    }

    public int currentHp() {
        return currentHp;
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

    public List<Characteristic> characteristics() {
        return new ArrayList<Characteristic>();
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

    public void addItem(Item item) {
        inventory.addItem(item);
    }

    public void removeItem(Item item) {
        inventory.removeItem(item);
    }

    public boolean hasItem(Item item) {
        return inventory.contains(item);
    }

    public int getInventorySize() {
        return inventory.size();
    }

    public int maxHp() {
        int constitutionModifier = getCharacteristic(Characteristics.CONSTITUTION)
                .map(Characteristic::modifier)
                .orElse(0);
        return Math.max(1, role.baseHp() + constitutionModifier);
    }
}
