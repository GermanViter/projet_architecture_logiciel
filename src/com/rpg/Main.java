package com.rpg;
import com.rpg.domain.hero.Hero;
import com.rpg.domain.hero.HeroFactory;
import com.rpg.domain.hero.HeroName;
import com.rpg.domain.hero.characteristics.Characteristic;
import com.rpg.domain.hero.characteristics.Characteristics;
import com.rpg.domain.hero.role.CharacterRole;
import com.rpg.domain.hero.role.Warrior;
import com.rpg.domain.hero.species.Species;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        HeroName name = readName(scanner);
        Species species = readSpecies(scanner);
        ArrayList<Characteristic> characteristics = readCharacteristics(scanner);
        CharacterRole role = new Warrior();

        Hero hero = new HeroFactory().create(name, species, role, characteristics);
        System.out.println(hero.name().getName() + " - level " + hero.level());
        hero.characteristics().forEach(System.out::println);
    }

    private static HeroName readName(Scanner scanner) {
        while (true) {
            System.out.print("Hero name: ");
            String input = scanner.nextLine();
            try {
                return new HeroName(input);
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static Species readSpecies(Scanner scanner) {
        while (true) {
            System.out.print("Species (HUMAN, ELF, DWARF, ORC): ");
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                return Species.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: unknown species.");
            }
        }
    }

    private static ArrayList<Characteristic> readCharacteristics(Scanner scanner) {
        ArrayList<Characteristic> characteristics = new ArrayList<>();
        for (Characteristics type : Characteristics.values()) {
            int value = readCharacteristicValue(scanner, type);
            characteristics.add(new Characteristic(type, value));
        }
        return characteristics;
    }

    private static int readCharacteristicValue(Scanner scanner, Characteristics type) {
        String label = type.name().charAt(0) + type.name().substring(1).toLowerCase();
        while (true) {
            System.out.printf("%s (between %d and %d): ", label, Characteristic.minimumCreationValue(), Characteristic.maximumCreationValue());
            String input = scanner.nextLine();
            try {
                int value = Integer.parseInt(input);
                new Characteristic(type, value);
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Error: please enter a whole number.");
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
