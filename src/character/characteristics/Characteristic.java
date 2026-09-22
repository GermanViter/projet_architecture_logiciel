package character.characteristics;

import java.util.Objects;

public final class Characteristic {
    private static final int MIN_VALUE_CREATION = 3;
    private static final int MAX_VALUE_CREATION = 18;
    private static final int MAX_VALUE_ABS = 20;

    private final Characteristics type;
    private final int value;

    public Characteristic(Characteristics type, int value) {
        if (type == null) {
            throw new IllegalArgumentException("Characteristic type cannot be null.");
        }
        if (value < MIN_VALUE_CREATION || value > MAX_VALUE_CREATION) {
            throw new InvalidCharacteristicException(value);
        }
        this.type = type;
        this.value = value;
    }

    private Characteristic(Characteristics type, int value, boolean skipValidation) {
        this.type = type;
        this.value = value;
    }

    public static Characteristic create(Characteristics type, int value) {
        return new Characteristic(type, value);
    }

    public Characteristic applyBonus(int bonus) {
        int newValue = Math.min(value + bonus, MAX_VALUE_ABS);
        return new Characteristic(this.type, newValue, true);
    }

    public Characteristics type() {
        return type;
    }

    public int value() {
        return value;
    }

    public int modifier() {
        return type.modifier(value);
    }

    public int initiativeBonus() {
        return type.getInitiativeBonus(value);
    }

    public int armorBonus(boolean allowsDexBonus) {
        return type.getArmorBonus(value, allowsDexBonus);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Characteristic)) return false;
        Characteristic that = (Characteristic) other;
        return this.value == that.value && this.type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }

    @Override
    public String toString() {
        return type + ": " + value + " (" + (modifier() >= 0 ? "+" : "") + modifier() + ")";
    }
}
