package fr.univcotedazur.teamj.kiwicard.dto;

import fr.univcotedazur.teamj.kiwicard.entities.Item;

public record ItemDTO(String label, double price) {
    public ItemDTO(Item item) {
        this(item.getLabel(), item.getPrice());
    }
}
