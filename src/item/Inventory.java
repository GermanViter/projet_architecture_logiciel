package item;

import java.util.ArrayList;
import java.util.List;

public class Inventory {

    public static final int DEFAULT_CAPACITY = 10;

    private final int capacity;
    private final List<Item> items;

    public Inventory() {
        this(DEFAULT_CAPACITY);
    }

    public Inventory(int capacity) {
        this.capacity = capacity;
        this.items = new ArrayList<>();
    }

    public void addItem(Item item) {
        if (isFull()) {
            throw new InventoryFullException();
        }
        items.add(item);
    }

    public void removeItem(Item item) {
        if (!items.contains(item)) {
            throw new ItemNotOwnedException(item);
        }
        items.remove(item);
    }

    public boolean contains(Item item) {
        return items.contains(item);
    }

    public boolean isFull() {
        return items.size() >= capacity;
    }

    public int size() {
        return items.size();
    }
}
