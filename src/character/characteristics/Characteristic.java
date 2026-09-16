package character.characteristics;

public final class Characteristic {
    private static final int MIN_VALUE_CREATION = 3;
    private static final int MAX_VALUE_CREATION = 18;
    private static final int MAX_VALUE_ABS = 20;

    private final int value;

    private Characteristic(int value) {
        this.value = value;
    }

    public static Characteristic create(int value) {
        if (value < MIN_VALUE_CREATION || value > MAX_VALUE_CREATION) {
            throw new InvalidCharacteristicException(value);
        }
        return new Characteristic(value);
    }

    public Characteristic applyBonus(int bonus) {
        int newValue = Math.min(value + bonus, MAX_VALUE_ABS);
        return new Characteristic(newValue);
    }

    public int value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Characteristic)) return false;
        return this.value == ((Characteristic) other).value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
}



