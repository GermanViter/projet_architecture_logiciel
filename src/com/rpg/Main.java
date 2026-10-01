package com.rpg;
import com.rpg.domain.hero.HeroName;
import com.rpg.domain.hero.Hero;
import com.rpg.domain.hero.characteristics.Characteristic;
import com.rpg.domain.hero.characteristics.Characteristics;
import com.rpg.domain.hero.role.CharacterRole;
import com.rpg.domain.item.Inventory;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        HeroName name = readName(scanner);
        ArrayList<Characteristic> characteristics = readCharacteristics(scanner);
        CharacterRole role = CharacterRole.WARRIOR;
        Inventory inventory = new Inventory();

        Hero hero = new Hero(name, characteristics, role, inventory);
        System.out.println(hero.name().getName() + " - level " + hero.level());
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
            System.out.print(label + " (between 3 and 18): ");
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
