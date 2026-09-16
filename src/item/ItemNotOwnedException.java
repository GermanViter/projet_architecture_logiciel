package item;

public class ItemNotOwnedException extends RuntimeException {
    public ItemNotOwnedException(Item item) {
        super("The item \"" + item.getName() + "\" is not in the inventory.");
    }
}
