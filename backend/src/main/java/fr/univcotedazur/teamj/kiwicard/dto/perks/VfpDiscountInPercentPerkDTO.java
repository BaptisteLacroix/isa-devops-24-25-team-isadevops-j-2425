package fr.univcotedazur.teamj.kiwicard.dto.perks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.univcotedazur.teamj.kiwicard.mappers.PerkDTOVisitor;

import java.time.LocalTime;

@JsonTypeName("VfpDiscountInPercentPerkDTO")
@JsonIgnoreProperties(ignoreUnknown = true)
public record VfpDiscountInPercentPerkDTO(Long perkId, double discountRate, LocalTime startHour,
                                          LocalTime endHour) implements IPerkDTO {
    @Override
    public String toString() {
        return discountRate + "% de réduction pour tous les VFP lors de la réservation entre " + startHour + "h et " + endHour + "h";
    }

    @Override
    public <T> T accept(PerkDTOVisitor<T> visitor) {
        return visitor.fromDTO(this);
    }
}
