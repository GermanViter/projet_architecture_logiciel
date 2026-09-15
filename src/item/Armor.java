package item;

public class Armor  {

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


}
