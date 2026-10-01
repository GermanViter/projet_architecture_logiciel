package com.rpg.domain.item;
import com.rpg.domain.item.exceptions.*;
import com.rpg.domain.hero.characteristics.Characteristics;
import com.rpg.domain.hero.role.CharacterRole;
import com.rpg.domain.dice.Dice;
import java.util.Set;




public enum WeaponCategory {
    SWORD(new Dice(1, 8, 0), Characteristics.STRENGTH, Set.of(CharacterRole.WARRIOR)),
    AXE(new Dice(1, 10, 0), Characteristics.STRENGTH, Set.of(CharacterRole.WARRIOR)),
    MACE(new Dice(1, 6, 0), Characteristics.STRENGTH, Set.of(CharacterRole.WARRIOR)),
    DAGGER(new Dice(1, 4, 0), Characteristics.DEXTERITY, Set.of(CharacterRole.WARRIOR, CharacterRole.MAGE)),
    BOW(new Dice(1, 8, 0), Characteristics.DEXTERITY, Set.of(CharacterRole.WARRIOR)),
    STAFF(new Dice(1, 6, 0), Characteristics.STRENGTH, Set.of(CharacterRole.MAGE));

    private final Dice damage;
    private final Characteristics characteristic;
    private final Set<CharacterRole> allowedRoles;

    WeaponCategory(Dice damage, Characteristics characteristic, Set<CharacterRole> allowedRoles) {
        this.damage = damage;
        this.characteristic = characteristic;
        this.allowedRoles = allowedRoles;
    }

    public Dice damage() {
        return damage;
    }

    public Characteristics characteristic() {
        return characteristic;
    }

    public boolean canBeEquippedBy(CharacterRole role) {
        return allowedRoles.contains(role);
    }
}
