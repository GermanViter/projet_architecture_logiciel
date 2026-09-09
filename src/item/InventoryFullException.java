package item;

public class InventoryFullException extends RuntimeException {
    public InventoryFullException() {
        super("L'inventaire est plein : capacité maximale atteinte.");
    }
}
