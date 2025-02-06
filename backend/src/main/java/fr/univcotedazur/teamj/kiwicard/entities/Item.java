package fr.univcotedazur.teamj.kiwicard.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Entity
public class Item {

    @Id
    @GeneratedValue
    private Long itemId;

    @NotNull
    @Column
    private String label;

    @NotNull
    @Column
    private double price;

    public Item() {
    }

    public Item(@NotNull String label, @NotNull double price) {
        this.label = label;
        this.price = price;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public @NotNull String getLabel() {
        return label;
    }

    public void setLabel(@NotNull String label) {
        this.label = label;
    }

    @NotNull
    public double getPrice() {
        return price;
    }

    public void setPrice(@NotNull double price) {
        this.price = price;
    }
}
