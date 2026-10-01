package com.rpg.domain.hero.exceptions;



public class InvalidWeaponException extends RuntimeException {
    public InvalidWeaponException(String name, int damage) {

        super("the weapon \"" + name + "\" has invalid damage : " + damage);
    }
}
