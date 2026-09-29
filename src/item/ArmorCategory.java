package item;

public enum ArmorCategory {
    NONE(10, true),
    LIGHT(11, true),
    HEAVY(16, false);

    private final int baseProtection;
    private final boolean allowsDexterityBonus;

    ArmorCategory(int baseProtection, boolean allowsDexterityBonus) {
        this.baseProtection = baseProtection;
        this.allowsDexterityBonus = allowsDexterityBonus;
    }

    public int baseProtection() {
        return baseProtection;
    }

    public boolean allowsDexterityBonus() {
        return allowsDexterityBonus;
    }
}