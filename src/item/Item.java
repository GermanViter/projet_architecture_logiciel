package item;

public class Item implements Equipable, Usable  {
    String name;

    public Item(String name) {
        this.name = name;
    }

    @Override
    public void use() {

    }

    @Override
    public void equip() {

    }
}
