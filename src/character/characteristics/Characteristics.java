package character.characteristics;

public final class Characteristics {
    private final Characteristic strength;
    private final Characteristic dexterity;
    private final Characteristic constitution;
    private final Characteristic intelligence;
    private final Characteristic wisdom;
    private final Characteristic charisma;

    private Characteristics(Builder builder) {
        this.strength = builder.strength;
        this.dexterity = builder.dexterity;
        this.constitution = builder.constitution;
        this.intelligence = builder.intelligence;
        this.wisdom = builder.wisdom;
        this.charisma = builder.charisma;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Characteristic strength() { return strength; }
    public Characteristic dexterity() { return dexterity; }
    public Characteristic constitution() { return constitution; }
    public Characteristic intelligence() { return intelligence; }
    public Characteristic wisdom() { return wisdom; }
    public Characteristic charisma() { return charisma; }

    public static final class Builder {
        private Characteristic strength;
        private Characteristic dexterity;
        private Characteristic constitution;
        private Characteristic intelligence;
        private Characteristic wisdom;
        private Characteristic charisma;

        private Builder() {
        }

        public Builder strength(int value) {
            this.strength = Characteristic.create(value);
            return this;
        }

        public Builder dexterity(int value) {
            this.dexterity = Characteristic.create(value);
            return this;
        }

        public Builder constitution(int value) {
            this.constitution = Characteristic.create(value);
            return this;
        }

        public Builder intelligence(int value) {
            this.intelligence = Characteristic.create(value);
            return this;
        }

        public Builder wisdom(int value) {
            this.wisdom = Characteristic.create(value);
            return this;
        }

        public Builder charisma(int value) {
            this.charisma = Characteristic.create(value);
            return this;
        }

        public Characteristics build() {
            if (strength == null || dexterity == null || constitution == null
                    || intelligence == null || wisdom == null || charisma == null) {
                throw new IncompleteCharacteristicsException();
            }
            return new Characteristics(this);
        }
    }
}