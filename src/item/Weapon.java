package item;

import dice.Dice;

public class Weapon  {

    private final String name;
    private final Dice damage;
    private final WeaponCategory category;

    public Weapon(String name, Dice damage, WeaponCategory category) {
        this.name = name;
        this.damage = damage;
        this.category = category;
    }


    public String getName() {
        return name;
    }

    public Dice getDamage() {
        return damage;
    }

    public WeaponCategory getCategory() {
        return category;
    }


}
