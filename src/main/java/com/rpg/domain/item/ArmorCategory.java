package com.rpg.domain.item;

public enum ArmorCategory {
    NONE(10, Integer.MAX_VALUE),
    LEATHER(11, Integer.MAX_VALUE),
    CHAINMAIL(16, 0);

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
