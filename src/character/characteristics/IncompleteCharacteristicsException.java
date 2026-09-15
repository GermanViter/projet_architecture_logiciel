package character.characteristics;

public class IncompleteCharacteristicsException extends RuntimeException {
    public IncompleteCharacteristicsException() {
        super("The characteristic is not complete. all 6 of the characteristic vlaues have to be define.");
    }
}
