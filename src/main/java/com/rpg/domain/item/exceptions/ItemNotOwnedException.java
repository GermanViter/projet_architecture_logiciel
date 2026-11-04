package com.rpg.domain.item.exceptions;
import com.rpg.domain.item.Item;
import java.util.*;


public class ItemNotOwnedException extends RuntimeException {
    public ItemNotOwnedException(Item item) {
        super("The item \"" + item.getName() + "\" is not in the inventory.");
    }
}
