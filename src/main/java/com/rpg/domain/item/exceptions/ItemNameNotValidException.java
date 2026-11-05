package com.rpg.domain.item.exceptions;



public class ItemNameNotValidException extends RuntimeException {
    public ItemNameNotValidException(String name) {
        super("The item name \"" + name + "\" is not valid.");
    }
}
