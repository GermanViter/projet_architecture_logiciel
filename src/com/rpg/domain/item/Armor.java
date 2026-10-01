package com.rpg.domain.item;
import com.rpg.domain.item.exceptions.*;
import com.rpg.domain.hero.role.CharacterRole;
import java.util.Objects;




public class Armor implements Equipable {

    private final ItemName name;
    private final ArmorCategory category;

    public Armor(ItemName name, ArmorCategory category) {
        this.name = name;
        this.category = Objects.requireNonNull(category, "Armor category cannot be null.");
    }

    public int computeArmorClass(int dexterityModifier) {
        int cappedDex = Math.min(dexterityModifier, category.maxDexBonus());
        return category.baseProtection() + Math.max(0, cappedDex);
    }

    @Override
    public String getName() {
        return name.value();
    }

    public ArmorCategory getCategory() {
        return category;
    }

    @Override
    public boolean canBeEquippedBy(CharacterRole role) {
        return role.canEquip(category);
    }

    @Override
    public String toString() {
        return name + " (base " + category.baseProtection()
                + (category.allowsDexterityBonus() ? ", + Dexterity" : ", no Dexterity bonus") + ")";
    }
}
