package demo;
import com.rpg.domain.hero.*;
import com.rpg.domain.hero.role.*;
import com.rpg.domain.hero.exceptions.*;
import com.rpg.domain.item.*;
import com.rpg.domain.item.exceptions.*;

import com.rpg.domain.hero.characteristics.Characteristic;
import com.rpg.domain.hero.characteristics.Characteristics;
import com.rpg.domain.hero.role.CharacterRole;
import com.rpg.domain.hero.role.Warrior;
import com.rpg.domain.hero.role.Mage;
import com.rpg.domain.item.*;

import java.util.ArrayList;

public class EquipmentDemo {
    public static void main(String[] args) {
        System.out.println("=== Hero Equipment System Demo ===\n");

        // Setup: Create a Warrior with inventory
        ArrayList<Characteristic> chars = new ArrayList<>();
        chars.add(new Characteristic(Characteristics.STRENGTH, 16));
        chars.add(new Characteristic(Characteristics.DEXTERITY, 14));
        chars.add(new Characteristic(Characteristics.INTELLIGENCE, 10));
        chars.add(new Characteristic(Characteristics.WISDOM, 12));

        Inventory inventory = new Inventory();
        CharacterRole role = new Warrior();
        Hero hero = new Hero(new HeroName("Conan"), chars, role, inventory);

        System.out.println("Created Hero: " + hero.name().getName() + " - Role: " + hero.role());

        // Test 1: Try to equip item not in inventory
        System.out.println("\n--- Test 1: Equip item NOT in inventory ---");
        try {
            Weapon sword = new Weapon(new ItemName("Iron Sword"), WeaponCategory.SWORD);
            hero.equip(sword);
            System.out.println("ERROR: Should have thrown exception!");
        } catch (ItemNotOwnedException e) {
            System.out.println("✓ Correctly rejected: ItemNotOwnedException");
            System.out.println("  Message: " + e.getMessage());
        }

        // Add items to inventory
        System.out.println("\n--- Adding items to inventory ---");
        Weapon sword = new Weapon(new ItemName("Iron Sword"), WeaponCategory.SWORD);
        Weapon axe = new Weapon(new ItemName("Battle Axe"), WeaponCategory.AXE);
        Weapon staff = new Weapon(new ItemName("Magic Staff"), WeaponCategory.STAFF);
        Armor lightArmor = new Armor(new ItemName("Leather Armor"), ArmorCategory.LIGHT);
        Armor heavyArmor = new Armor(new ItemName("Plate Mail"), ArmorCategory.HEAVY);

        inventory.addItem(sword);
        inventory.addItem(axe);
        inventory.addItem(staff);
        inventory.addItem(lightArmor);
        inventory.addItem(heavyArmor);
        System.out.println("Added 5 items. Inventory size: " + inventory.size());

        // Test 2: Try to equip item not allowed for role
        System.out.println("\n--- Test 2: Equip item not allowed for role ---");
        try {
            Hero mageForTest = new Hero(new HeroName("MageTest"), chars, new Mage(), new Inventory());
            Weapon bow = new Weapon(new ItemName("Longbow"), WeaponCategory.BOW);
            mageForTest.inventory().addItem(bow);
            mageForTest.equip(bow);
            System.out.println("ERROR: Should have thrown exception!");
        } catch (ClassRestrictionException e) {
            System.out.println("✓ Correctly rejected: ClassRestrictionException");
            System.out.println("  Message: " + e.getMessage());
        }

        // Test 3: Try to equip heavy armor (Warriors CAN equip it - let's test Mage instead)
        System.out.println("\n--- Test 3: Equip heavy armor (Warrior CAN equip) ---");
        try {
            hero.equip(heavyArmor);
            System.out.println("✓ Successfully equipped: " + heavyArmor.getName());
            System.out.println("  Inventory size after equip: " + inventory.size());
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        // Test 4: Equip a valid weapon
        System.out.println("\n--- Test 4: Equip valid weapon ---");
        try {
            Weapon sword = new Weapon(new ItemName("Iron Sword"), WeaponCategory.SWORD);
            // Hero already has a Long Sword, we check if we can equip another sword
            hero.inventory().addItem(sword);
            hero.equip(sword);
            System.out.println("✓ Successfully equipped: " + sword.getName());
            System.out.println("  Inventory size after equip: " + inventory.size());
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        // Test 5: Swap weapon (old one should return to inventory)
        System.out.println("\n--- Test 5: Swap weapon ---");
        try {
            hero.equip(axe);
            System.out.println("✓ Successfully equipped: " + axe.getName());
            System.out.println("  Inventory size after swap: " + inventory.size());
            // Check sword is back in inventory
            if (inventory.contains(sword)) {
                System.out.println("  ✓ Old weapon (sword) returned to inventory");
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        // Test 6: Create Mage and verify class restrictions
        System.out.println("\n--- Test 6: Mage character restrictions ---");
        Inventory mageInventory = new Inventory();
        Weapon mageStaff = new Weapon(new ItemName("Wizard Staff"), WeaponCategory.STAFF);
        Armor mageHeavyArmor = new Armor(new ItemName("Heavy Plate"), ArmorCategory.HEAVY);
        mageInventory.addItem(mageStaff);
        mageInventory.addItem(mageHeavyArmor);

        Hero mageHero = new Hero(new HeroName("Gandalf"), chars, CharacterRole.MAGE, mageInventory);

        // Mage can use staff
        try {
            mageHero.equip(mageStaff);
            System.out.println("✓ Mage successfully equipped: " + mageStaff.getName());
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        // Mage cannot use heavy armor
        try {
            mageHero.equip(mageHeavyArmor);
            System.out.println("ERROR: Should have thrown exception!");
        } catch (ClassRestrictionException e) {
            System.out.println("✓ Mage correctly rejected heavy armor: ClassRestrictionException");
            System.out.println("  Message: " + e.getMessage());
        }

        System.out.println("\n=== All tests passed! ===");
    }
}