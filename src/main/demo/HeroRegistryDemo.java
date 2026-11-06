package demo;

import com.rpg.domain.hero.Hero;
import com.rpg.domain.hero.HeroName;
import com.rpg.domain.hero.HeroRegistry;
import com.rpg.domain.hero.characteristics.Characteristic;
import com.rpg.domain.hero.characteristics.Characteristics;
import com.rpg.domain.hero.exceptions.DuplicateHeroNameException;
import com.rpg.domain.hero.role.Mage;
import com.rpg.domain.hero.role.Warrior;
import com.rpg.domain.item.Inventory;

import java.util.ArrayList;
import java.util.List;

public class HeroRegistryDemo {
    public static void main(String[] args) {
        System.out.println("=== Hero Registry Demo ===\n");
        boolean hasFailed = false;

        ArrayList<Characteristic> chars = new ArrayList<>();
        chars.add(new Characteristic(Characteristics.STRENGTH, 16));
        chars.add(new Characteristic(Characteristics.DEXTERITY, 14));
        chars.add(new Characteristic(Characteristics.INTELLIGENCE, 10));
        chars.add(new Characteristic(Characteristics.WISDOM, 12));

        HeroRegistry registry = new HeroRegistry();

        // Test 1: Add hero to empty registry
        System.out.println("--- Test 1: Add hero to registry ---");
        try {
            Hero conan = new Hero(new HeroName("Conan"), chars, new Warrior(), new Inventory());
            registry.addHero(conan);
            List<Hero> heroes = registry.allHeroes();
            if (heroes.size() == 1 && heroes.get(0).name().equals(conan.name())) {
                System.out.println("✓ Successfully added hero: " + conan.name().getName());
                System.out.println("  Registered heroes count: " + heroes.size());
            } else {
                hasFailed = true;
                System.out.println("ERROR: Hero not found in registry as expected!");
            }
        } catch (Exception e) {
            hasFailed = true;
            System.out.println("ERROR: Unexpected exception: " + e.getMessage());
        }

        // Test 2: Reject duplicate hero name
        System.out.println("\n--- Test 2: Reject duplicate hero name ---");
        try {
            Hero duplicateConan = new Hero(new HeroName("Conan"), chars, new Mage(), new Inventory());
            registry.addHero(duplicateConan);
            hasFailed = true;
            System.out.println("ERROR: Should have thrown DuplicateHeroNameException!");
        } catch (DuplicateHeroNameException e) {
            System.out.println("✓ Correctly rejected duplicate name: DuplicateHeroNameException");
            System.out.println("  Message: " + e.getMessage());
        }

        // Test 3: Add second hero with distinct name
        System.out.println("\n--- Test 3: Add second hero with distinct name ---");
        try {
            Hero gandalf = new Hero(new HeroName("Gandalf"), chars, new Mage(), new Inventory());
            registry.addHero(gandalf);
            List<Hero> heroes = registry.allHeroes();
            if (heroes.size() == 2) {
                System.out.println("✓ Successfully added second hero: " + gandalf.name().getName());
                System.out.println("  Registered heroes count: " + heroes.size());
            } else {
                hasFailed = true;
                System.out.println("ERROR: Expected 2 heroes in registry, found: " + heroes.size());
            }
        } catch (Exception e) {
            hasFailed = true;
            System.out.println("ERROR: Unexpected exception: " + e.getMessage());
        }

        // Test 4: Defensive copy verification
        System.out.println("\n--- Test 4: Defensive copy of allHeroes() ---");
        try {
            List<Hero> heroesCopy = registry.allHeroes();
            Hero temporaryHero = new Hero(new HeroName("Temporary"), chars, new Warrior(), new Inventory());
            heroesCopy.add(temporaryHero);

            if (registry.allHeroes().size() == 2) {
                System.out.println("✓ Mutating returned list does not affect registry (defensive copy confirmed)");
                System.out.println("  Registry count remains: " + registry.allHeroes().size());
            } else {
                hasFailed = true;
                System.out.println("ERROR: Registry was mutated by external list modification!");
            }
        } catch (Exception e) {
            hasFailed = true;
            System.out.println("ERROR: Unexpected exception: " + e.getMessage());
        }

        // Display all heroes from registry (caller handles display)
        System.out.println("\n--- Registered Heroes List (presentation by caller) ---");
        for (Hero hero : registry.allHeroes()) {
            System.out.println("- " + hero.name().getName() + " (Level " + hero.level() + " " + hero.role().getClass().getSimpleName() + ")");
        }

        System.out.println(hasFailed ? "\n=== Some tests FAILED! ===" : "\n=== All tests passed! ===");
    }
}
