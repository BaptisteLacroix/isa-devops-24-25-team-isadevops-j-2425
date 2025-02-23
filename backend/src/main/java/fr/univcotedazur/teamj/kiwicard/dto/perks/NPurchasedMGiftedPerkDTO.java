package fr.univcotedazur.teamj.kiwicard.dto.perks;

import fr.univcotedazur.teamj.kiwicard.dto.ItemDTO;

public record NPurchasedMGiftedPerkDTO(Long perkId, int nbPurchased, ItemDTO item, int nbGifted, Long itemId) implements IPerkDTO {
    @Override
    public String toString() {
        return "Buy " + nbPurchased + " " + item.label() + " and get " + nbGifted + " for free";
    }
}
