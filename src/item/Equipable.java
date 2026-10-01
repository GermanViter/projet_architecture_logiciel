package item;

import character.role.CharacterRole;

public interface Equipable extends Item {
    boolean canBeEquippedBy(CharacterRole role);
}
