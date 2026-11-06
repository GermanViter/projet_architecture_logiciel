package com.rpg.domain.creature;

import com.rpg.domain.dice.Dice;
import com.rpg.domain.dice.DiceRoller;
import java.util.Objects;

public final class Creature {

    private static final int INITIATIVE_DIE_SIDES = 20;
    private static final int MIN_HEALTH = 0;

    private final String name;
    private final CreatureCategory category;
    private int currentHealth;

    public Creature(String name, CreatureCategory category) {
        this.name = Objects.requireNonNull(name, "Creature name cannot be null.");
        this.category = Objects.requireNonNull(category, "Creature category cannot be null.");
        this.currentHealth = category.baseHealth();
    }

    public Creature(CreatureCategory category) {
        this(category.displayName(), category);
    }

    public void takeDamage(int amount) {
        if (amount <= 0) {
            return;
        }
        this.currentHealth = Math.max(MIN_HEALTH, this.currentHealth - amount);
    }

    public boolean isAlive() {
        return currentHealth > MIN_HEALTH;
    }

    public boolean isDefeated() {
        return currentHealth == MIN_HEALTH;
    }

    public int rollInitiative(DiceRoller roller) {
        return roller.roll(INITIATIVE_DIE_SIDES) + category.initiativeBonus();
    }

    public int attack(DiceRoller roller) {
        return category.damage().roll(roller);
    }

    public String name() {
        return name;
    }

    public CreatureCategory category() {
        return category;
    }

    public int currentHealth() {
        return currentHealth;
    }

    public int maxHealth() {
        return category.baseHealth();
    }

    public int armorClass() {
        return category.armorClass();
    }

    public int initiativeBonus() {
        return category.initiativeBonus();
    }

    public int experienceReward() {
        return category.experienceReward();
    }

    public String attackName() {
        return category.attackName();
    }

    public int attackBonus() {
        return category.attackBonus();
    }

    public Dice damage() {
        return category.damage();
    }
}
