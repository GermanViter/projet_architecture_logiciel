package item;

import character.role.CharacterRole;

import java.util.Set;

public enum ArmorCategory {
    NONE(10, true, Set.of(CharacterRole.WARRIOR, CharacterRole.MAGE)),
    LIGHT(11, true, Set.of(CharacterRole.WARRIOR, CharacterRole.MAGE)),
    HEAVY(16, false, Set.of(CharacterRole.WARRIOR));

    private final int baseProtection;
    private final boolean allowsDexterityBonus;
    private final Set<CharacterRole> allowedRoles;

    ArmorCategory(int baseProtection, boolean allowsDexterityBonus, Set<CharacterRole> allowedRoles) {
        this.baseProtection = baseProtection;
        this.allowsDexterityBonus = allowsDexterityBonus;
        this.allowedRoles = allowedRoles;
    }

    public int baseProtection() {
        return baseProtection;
    }

    public boolean allowsDexterityBonus() {
        return allowsDexterityBonus;
    }

    public boolean canBeEquippedBy(CharacterRole role) {
        return allowedRoles.contains(role);
    }
}