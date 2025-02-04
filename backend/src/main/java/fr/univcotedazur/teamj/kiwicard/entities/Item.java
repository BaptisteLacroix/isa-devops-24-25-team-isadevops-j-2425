package fr.univcotedazur.teamj.kiwicard.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Embeddable
public class Item {


    @NotNull
    private int quantity;

    public Item() {}

    public Item(int quantity) {
        this.quantity = quantity;
    }


    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() { return quantity + "x"; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
        return quantity == item.quantity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantity);
    }
}
