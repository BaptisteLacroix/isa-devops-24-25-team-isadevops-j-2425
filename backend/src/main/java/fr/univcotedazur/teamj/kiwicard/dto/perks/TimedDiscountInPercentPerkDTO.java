package fr.univcotedazur.teamj.kiwicard.dto.perks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.univcotedazur.teamj.kiwicard.mappers.PerkDTOVisitor;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@JsonTypeName("TimedDiscountInPercentPerkDTO")
@JsonIgnoreProperties(ignoreUnknown = true)
public record TimedDiscountInPercentPerkDTO(Long perkId, LocalTime time, double discountRate) implements IPerkDTO {
    @Override
    public String toString() {
        return "Remise de " + discountRate + "% après " + time.format(DateTimeFormatter.ofPattern("HH:mm")) + " sur tous les articles";
    }

    @Override
    public <T> T accept(PerkDTOVisitor<T> visitor) {
        return visitor.fromDTO(this);
    }
}
