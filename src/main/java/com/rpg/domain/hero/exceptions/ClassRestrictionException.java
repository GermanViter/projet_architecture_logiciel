package com.rpg.domain.hero.exceptions;
import com.rpg.domain.hero.role.CharacterRole;



public class ClassRestrictionException extends RuntimeException {
    public ClassRestrictionException(String itemName, CharacterRole role) {
        super(String.format("Character of role %s cannot equip item: %s", role.getClass().getSimpleName(), itemName));
    }
}
