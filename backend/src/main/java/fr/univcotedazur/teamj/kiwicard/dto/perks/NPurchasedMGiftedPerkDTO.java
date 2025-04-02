package fr.univcotedazur.teamj.kiwicard.dto.perks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.univcotedazur.teamj.kiwicard.dto.ItemDTO;
import fr.univcotedazur.teamj.kiwicard.mappers.PerkDTOVisitor;

@JsonTypeName("NPurchasedMGiftedPerkDTO")
@JsonIgnoreProperties(ignoreUnknown = true)
public record NPurchasedMGiftedPerkDTO(Long perkId, int nbPurchased, ItemDTO item, int nbGifted) implements IPerkDTO {
    @Override
    public String toString() {
        return "Achetez " + nbPurchased + " " + item.label() + " et obtenez " + nbGifted + " gratuitement";
    }

    @Override
    public <T> T accept(PerkDTOVisitor<T> visitor) {
        return visitor.fromDTO(this);
    }
}
