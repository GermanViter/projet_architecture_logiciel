package item;
import java.util.ArrayList;

public class Inventory {
    private int capacity;
    private ArrayList<Item> items;

    void Inventory(int capacity){
        this.capacity = capacity;
        this.items = new ArrayList<>();
    }

    public void  addItem(Item item){
        if (!this.items.contains(item) && this.items.size() < capacity) {
            this.items.add(item);
        }
    }

    public void  removeItem(Item item){
        if (this.items.contains(item)) {
            this.items.remove(item);
        }
    }
}
