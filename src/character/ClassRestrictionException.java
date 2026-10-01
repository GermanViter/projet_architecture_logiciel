package character;

import character.role.CharacterRole;

public class ClassRestrictionException extends RuntimeException {
    public ClassRestrictionException(String itemName, CharacterRole role) {
        super(String.format("Character of role %s cannot equip item: %s", role, itemName));
    }
}
