package com.rpg.domain.item;

import com.rpg.domain.dice.Dice;

public class HealingPotion extends Potion {

    public HealingPotion(ItemName name) {
        super(new Dice(2, 4, 2), name.value());
    }

    public HealingPotion() {
        super(new Dice(2, 4, 2), "Healing Potion");
    }
}
