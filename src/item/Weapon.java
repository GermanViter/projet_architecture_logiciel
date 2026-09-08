package item;

import dice.Dice;

public class Weapon implements Equipable {

    private final String name;
    private final Dice damage;
    private final WeaponCategory category;

    public Weapon(String name, Dice damage, WeaponCategory category) {
        this.name = name;
        this.damage = damage;
        this.category = category;
    }

    @Override
    public String getName() {
        return name;
    }

    public Dice getDamage() {
        return damage;
    }

    public WeaponCategory getCategory() {
        return category;
    }

    @Override
    public void equip() {

    }
}
