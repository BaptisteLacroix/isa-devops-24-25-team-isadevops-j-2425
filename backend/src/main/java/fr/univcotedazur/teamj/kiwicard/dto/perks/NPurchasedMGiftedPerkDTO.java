package fr.univcotedazur.teamj.kiwicard.dto.perks;

import fr.univcotedazur.teamj.kiwicard.dto.ItemDTO;
import fr.univcotedazur.teamj.kiwicard.mappers.PerkDTOVisitor;

public record NPurchasedMGiftedPerkDTO(Long perkId, int nbPurchased, ItemDTO item, int nbGifted) implements IPerkDTO {
    @Override
    public String toString() {
        return "Buy " + nbPurchased + " " + item.label() + " and get " + nbGifted + " for free";
    }

    @Override
    public <T> T accept(PerkDTOVisitor<T> visitor) {
        return visitor.fromDTO(this);
    }
}
