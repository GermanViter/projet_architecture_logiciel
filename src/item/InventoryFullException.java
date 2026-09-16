package item;

public class InventoryFullException extends RuntimeException {
    public InventoryFullException() {
        super("Inventory is full: maximum capacity reached.");
    }
}
