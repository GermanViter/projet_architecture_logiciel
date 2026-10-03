package demo;

import com.rpg.domain.hero.Hero;
import com.rpg.domain.hero.HeroName;
import com.rpg.domain.hero.characteristics.Characteristic;
import com.rpg.domain.hero.characteristics.Characteristics;
import com.rpg.domain.hero.exceptions.ClassRestrictionException;
import com.rpg.domain.hero.role.Mage;
import com.rpg.domain.hero.role.Warrior;
import com.rpg.domain.item.Armor;
import com.rpg.domain.item.ArmorCategory;
import com.rpg.domain.item.Inventory;
import com.rpg.domain.item.ItemName;
import com.rpg.domain.item.Weapon;
import com.rpg.domain.item.WeaponCategory;
import com.rpg.domain.item.exceptions.ItemNotOwnedException;

import java.util.ArrayList;

public class EquipmentDemo {
    public static void main(String[] args) {
        System.out.println("=== Hero Equipment System Demo ===\n");
        boolean hasFailed = false;

        ArrayList<Characteristic> chars = new ArrayList<>();
        chars.add(new Characteristic(Characteristics.STRENGTH, 16));
        chars.add(new Characteristic(Characteristics.DEXTERITY, 14));
        chars.add(new Characteristic(Characteristics.INTELLIGENCE, 10));
        chars.add(new Characteristic(Characteristics.WISDOM, 12));

        // Warrior starts with Long Sword + Mesh Armor in inventory automatically
        Inventory inventory = new Inventory();
        Hero hero = new Hero(new HeroName("Conan"), chars, new Warrior(), inventory);
        System.out.println("Created Warrior: " + hero.name().getName() + " (maxHp: " + hero.maxHp() + ")");
        System.out.println("Starting inventory size: " + inventory.size());

        // Test 1: Equip item NOT in inventory
        System.out.println("\n--- Test 1: Equip item NOT in inventory ---");
        try {
            Weapon unknownSword = new Weapon(new ItemName("Unknown Sword"), WeaponCategory.SWORD);
            hero.equip(unknownSword);
            hasFailed = true;
            System.out.println("ERROR: Should have thrown exception!");
        } catch (ItemNotOwnedException e) {
            System.out.println("✓ Correctly rejected: ItemNotOwnedException");
        }

        // Test 2: Mage cannot equip a BOW (class restriction)
        System.out.println("\n--- Test 2: Mage cannot equip a BOW ---");
        try {
            Inventory mageInventory = new Inventory();
            Hero mage = new Hero(new HeroName("Gandalf"), chars, new Mage(), mageInventory);
            Weapon bow = new Weapon(new ItemName("Longbow"), WeaponCategory.BOW);
            mageInventory.addItem(bow);
            mage.equip(bow);
            hasFailed = true;
            System.out.println("ERROR: Should have thrown exception!");
        } catch (ClassRestrictionException e) {
            System.out.println("✓ Correctly rejected: ClassRestrictionException");
            System.out.println("  Message: " + e.getMessage());
        }

        // Test 3: Warrior equips heavy armor
        System.out.println("\n--- Test 3: Warrior equips heavy armor ---");
        try {
            Armor plateArmor = new Armor(new ItemName("Plate Mail"), ArmorCategory.CHAINMAIL);
            inventory.addItem(plateArmor);
            hero.equip(plateArmor);
            System.out.println("✓ Successfully equipped: " + plateArmor.getName());
            System.out.println("  Inventory size: " + inventory.size());
        } catch (Exception e) {
            hasFailed = true;
            System.out.println("ERROR: " + e.getMessage());
        }

        // Test 4: Warrior equips a weapon
        System.out.println("\n--- Test 4: Warrior equips axe ---");
        try {
            Weapon axe = new Weapon(new ItemName("Battle Axe"), WeaponCategory.AXE);
            inventory.addItem(axe);
            hero.equip(axe);
            System.out.println("✓ Successfully equipped: " + axe.getName());
        } catch (Exception e) {
            hasFailed = true;
            System.out.println("ERROR: " + e.getMessage());
        }

        // Test 5: Swap weapon — old one returns to inventory
        System.out.println("\n--- Test 5: Swap weapon ---");
        try {
            Weapon sword = new Weapon(new ItemName("Iron Sword"), WeaponCategory.SWORD);
            inventory.addItem(sword);
            int sizeBefore = inventory.size();
            hero.equip(sword);
            System.out.println("✓ Swapped to: " + sword.getName());
            System.out.println("  Inventory size before: " + sizeBefore + ", after: " + inventory.size());
        } catch (Exception e) {
            hasFailed = true;
            System.out.println("ERROR: " + e.getMessage());
        }

        // Test 6: Mage cannot use heavy armor
        System.out.println("\n--- Test 6: Mage cannot equip heavy armor ---");
        try {
            Inventory mageInventory = new Inventory();
            Hero mage = new Hero(new HeroName("Merlin"), chars, new Mage(), mageInventory);
            Armor heavyArmor = new Armor(new ItemName("Heavy Plate"), ArmorCategory.CHAINMAIL);
            mageInventory.addItem(heavyArmor);
            mage.equip(heavyArmor);
            hasFailed = true;
            System.out.println("ERROR: Should have thrown exception!");
        } catch (ClassRestrictionException e) {
            System.out.println("✓ Mage correctly rejected heavy armor: ClassRestrictionException");
            System.out.println("  Message: " + e.getMessage());
        }

        System.out.println(hasFailed ? "\n=== Some tests FAILED! ===" : "\n=== All tests passed! ===");
    }
}
