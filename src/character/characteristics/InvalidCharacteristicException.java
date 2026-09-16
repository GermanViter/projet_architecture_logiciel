package character.characteristics;

public class InvalidCharacteristicException extends RuntimeException {
    public InvalidCharacteristicException(int value) {
        super("The characteristic value " + value + " is not valid.");
    }
}
