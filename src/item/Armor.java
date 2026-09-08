package item;

public class Armor implements Equipable {

    private final String name;
    private final int baseProtection;
    private final boolean allowsDexterityBonus;
    private final ArmorCategory category;

    public Armor(String name, int baseProtection, boolean allowsDexterityBonus, ArmorCategory category) {
        this.name = name;
        this.baseProtection = baseProtection;
        this.allowsDexterityBonus = allowsDexterityBonus;
        this.category = category;
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
    public void equip() {
        // Même remarque que pour Weapon.equip() : la logique réelle vit dans Hero.
    }
}
