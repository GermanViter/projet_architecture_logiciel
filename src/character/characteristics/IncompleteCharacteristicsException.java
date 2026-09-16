package character.characteristics;

public class IncompleteCharacteristicsException extends RuntimeException {
    public IncompleteCharacteristicsException() {
        super("The characteristics are not complete. All 6 characteristic values must be defined.");
    }
}
