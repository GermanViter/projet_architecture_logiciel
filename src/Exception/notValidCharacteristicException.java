package character.characteristics;

public class notValidCharacteristicException extends RuntimeException {
    public notValidCharacteristicException(int value) {

        super("The characteristic value " + value + " is not valid.");
    }
}
