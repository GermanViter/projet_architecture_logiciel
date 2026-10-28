package com.rpg.domain.item;
import com.rpg.domain.item.exceptions.*;
import java.util.*;
import java.util.Objects;



public final class ItemName {

    private final String value;

    public ItemName(String value) {
        if (value == null || value.isBlank()) {
            throw new ItemNameNotValidException(value);
        }
        this.value = value.trim();
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof ItemName)) return false;
        return value.equals(((ItemName) other).value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
