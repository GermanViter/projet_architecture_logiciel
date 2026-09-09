package dice;

public class Dice {

    private final int numberOfDice;
    private final int sides;
    private final int bonus;

    public Dice(int numberOfDice, int sides, int bonus) {
        this.numberOfDice = numberOfDice;
        this.sides = sides;
        this.bonus = bonus;
    }

    public int roll(DiceRoller roller) {
        int total = bonus;
        for (int i = 0; i < numberOfDice; i++) {
            total += roller.roll(sides);
        }
        return total;
    }

    public Dice doubled() {
        return new Dice(numberOfDice * 2, sides, bonus);
    }

    @Override
    public String toString() {
        return numberOfDice + "d" + sides + (bonus != 0 ? " + " + bonus : "");
    }
}