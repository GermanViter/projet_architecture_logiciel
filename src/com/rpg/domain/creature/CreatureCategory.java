package com.rpg.domain.creature;
import com.rpg.domain.dice.Dice;
import java.util.Objects;

public enum CreatureCategory {
    GOBLIN(
            "Gobelin",
            7,
            12,
            2,
            50,
            "Dague rouillée",
            3,
            new Dice(1, 6, 1)
    ),
    ORC(
            "Orc",
            15,
            13,
            1,
            100,
            "Hache",
            4,
            new Dice(1, 8, 2)
    ),
    TROLL(
            "Troll",
            35,
            15,
            0,
            300,
            "Massue",
            6,
            new Dice(2, 6, 3)
    );

    private final String displayName;
    private final int baseHealth;
    private final int armorClass;
    private final int initiativeBonus;
    private final int experienceReward;
    private final String attackName;
    private final int attackBonus;
    private final Dice damage;

    CreatureCategory(
            String displayName,
            int baseHealth,
            int armorClass,
            int initiativeBonus,
            int experienceReward,
            String attackName,
            int attackBonus,
            Dice damage
    ) {
        this.displayName = displayName;
        this.baseHealth = baseHealth;
        this.armorClass = armorClass;
        this.initiativeBonus = initiativeBonus;
        this.experienceReward = experienceReward;
        this.attackName = attackName;
        this.attackBonus = attackBonus;
        this.damage = Objects.requireNonNull(damage, "Damage dice cannot be null.");
    }

    public String displayName() {
        return displayName;
    }

    public int baseHealth() {
        return baseHealth;
    }

    public int armorClass() {
        return armorClass;
    }

    public int initiativeBonus() {
        return initiativeBonus;
    }

    public int experienceReward() {
        return experienceReward;
    }

    public String attackName() {
        return attackName;
    }

    public int attackBonus() {
        return attackBonus;
    }

    public Dice damage() {
        return damage;
    }
}
