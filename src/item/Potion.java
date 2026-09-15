package item;
import dice.Dice;

public class Potion implements Usable {
    private final Dice healing;
    private final String name;

    public Potion(Dice healing, String name) {
        this.healing = healing;
        this.name = name;
    }

    @Override
    public void use() {

    }

    public Dice getHealing() {
        return healing;
    }


    @Override
    public String getName() {
        return name;
    }
}
