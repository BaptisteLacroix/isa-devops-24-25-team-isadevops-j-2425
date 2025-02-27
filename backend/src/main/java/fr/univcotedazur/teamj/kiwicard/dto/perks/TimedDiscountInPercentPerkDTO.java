package fr.univcotedazur.teamj.kiwicard.dto.perks;

import fr.univcotedazur.teamj.kiwicard.mappers.PerkDTOVisitor;

import java.time.LocalTime;

public record TimedDiscountInPercentPerkDTO(Long perkId, LocalTime time, double discountRate) implements IPerkDTO {
    @Override
    public String toString() {
        return "Discount of " + discountRate + "% after " + time + "on all items";
    }

    @Override
    public <T> T accept(PerkDTOVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
