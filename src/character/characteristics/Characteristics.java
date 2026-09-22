package character.characteristics;

public enum Characteristics {
    STRENGTH {
        @Override
        public int getInitiativeBonus(int score) {
            return 0;
        }

        @Override
        public int getArmorBonus(int score, boolean allowsDexBonus) {
            return 0;
        }
    },
    DEXTERITY {
        @Override
        public int getInitiativeBonus(int score) {
            return modifier(score);
        }

        @Override
        public int getArmorBonus(int score, boolean allowsDexBonus) {
            return allowsDexBonus ? modifier(score) : 0;
        }
    },
    INTELLIGENCE {
        @Override
        public int getInitiativeBonus(int score) {
            return 0;
        }

        @Override
        public int getArmorBonus(int score, boolean allowsDexBonus) {
            return 0;
        }
    },
    WISDOM {
        @Override
        public int getInitiativeBonus(int score) {
            return 0;
        }

        @Override
        public int getArmorBonus(int score, boolean allowsDexBonus) {
            return 0;
        }
    };

    public int modifier(int score) {
        return Math.floorDiv(score - 10, 2);
    }

    public abstract int getInitiativeBonus(int score);
    public abstract int getArmorBonus(int score, boolean allowsDexBonus);
}