package character.characteristics;

public class IncompleteCharacteristicsException extends RuntimeException {
    public IncompleteCharacteristicsException() {
        super("The characteristics are not complete. All 4 characteristic values (Strength, Dexterity, Intelligence, Wisdom) must be defined.");
    }
}
