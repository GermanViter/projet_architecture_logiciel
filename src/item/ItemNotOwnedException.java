package item;

public class ItemNotOwnedException extends RuntimeException {
    public ItemNotOwnedException(Item item) {
        super("L'objet \"" + item.getName() + "\" n'est pas dans l'inventaire.");
    }
}
