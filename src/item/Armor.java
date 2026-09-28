package item;

import java.util.Objects;

public class Armor implements Item {

    private final String name;
    private final int baseProtection;
    private final boolean allowsDexterityBonus;
    private final ArmorCategory category;

    public Armor(String name, int baseProtection, boolean allowsDexterityBonus, ArmorCategory category) {
        this.name = Objects.requireNonNull(name, "Armor name cannot be null.");
        this.baseProtection = baseProtection;
        this.allowsDexterityBonus = allowsDexterityBonus;
        this.category = Objects.requireNonNull(category, "Armor category cannot be null.");
    }

    public static Armor none() {
        return new Armor("No armor", 10, true, ArmorCategory.NONE);
    }

    public static Armor leather() {
        return new Armor("Leather armor", 11, true, ArmorCategory.LIGHT);
    }

    public static Armor chainMail() {
        return new Armor("Chain mail", 16, false, ArmorCategory.HEAVY);
    }

    public int armorClass(int dexterityModifier) {
        return baseProtection + (allowsDexterityBonus ? dexterityModifier : 0);
    }

    @Override
    public String getName() {
        return name;
    }

    public int getBaseProtection() {
        return baseProtection;
    }

    public boolean allowsDexterityBonus() {
        return allowsDexterityBonus;
    }

    public ArmorCategory getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return name;
    }
}