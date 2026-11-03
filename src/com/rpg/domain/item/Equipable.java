package com.rpg.domain.item;
import com.rpg.domain.item.exceptions.*;
import com.rpg.domain.hero.role.CharacterRole;
import com.rpg.domain.hero.Hero;



public interface Equipable extends Item {
    boolean canBeEquippedBy(CharacterRole role);
    void equip(Hero hero);
}
