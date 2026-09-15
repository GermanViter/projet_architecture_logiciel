
import character.HeroName;
import character.Hero;

import character.characteristics.Characteristic;
import character.characteristics.Characteristics;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        HeroName name = readName(scanner);
        Characteristics characteristics = readCharacteristics(scanner);

        Hero hero = new Hero(name, characteristics);
        System.out.println(hero.name().getName() + " - niveau " + hero.level());
    }

    private static HeroName readName(Scanner scanner) {
        while (true) {
            System.out.print("Nom du héros : ");
            String input = scanner.nextLine();
            try {
                return new HeroName(input);
            } catch (RuntimeException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
    }

    private static Characteristics readCharacteristics(Scanner scanner) {
        return Characteristics.builder()
                .strength(readCharacteristicValue(scanner, "strength"))
                .dexterity(readCharacteristicValue(scanner, "Dexterity"))
                .constitution(readCharacteristicValue(scanner, "Constitution"))
                .intelligence(readCharacteristicValue(scanner, "Intelligence"))
                .wisdom(readCharacteristicValue(scanner, "wisdom"))
                .charisma(readCharacteristicValue(scanner, "Charisma"))
                .build();
    }

    private static int readCharacteristicValue(Scanner scanner, String label) {
        while (true) {
            System.out.print(label + " (between 3 et 18) : ");
            String input = scanner.nextLine();
            try {
                int value = Integer.parseInt(input);
                Characteristic.create(value);
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Error : please enter a whole number.");
            } catch (RuntimeException e) {
                System.out.println("Error : " + e.getMessage());
            }
        }
    }
}
