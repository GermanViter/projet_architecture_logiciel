package com.rpg.domain.item;

public enum ArmorCategory {
    NONE(10, Integer.MAX_VALUE),
    LIGHT(11, Integer.MAX_VALUE),
    MEDIUM(14, 2),
    HEAVY(16, 0);

    private final int baseProtection;
    private final int maxDexBonus;

    ArmorCategory(int baseProtection, int maxDexBonus) {
        this.baseProtection = baseProtection;
        this.maxDexBonus = maxDexBonus;
    }

    public int baseProtection() {
        return baseProtection;
    }

    public int maxDexBonus() {
        return maxDexBonus;
    }

    public boolean allowsDexterityBonus() {
        return maxDexBonus > 0;
    }
}
