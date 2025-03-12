package fr.univcotedazur.teamj.kiwicard.dto;

import fr.univcotedazur.teamj.kiwicard.entities.Item;

public record ItemDTO(long itemId, String label, double price) {
    public ItemDTO(Item item) {
        this(item.getItemId(), item.getLabel(), item.getPrice());
    }
}
