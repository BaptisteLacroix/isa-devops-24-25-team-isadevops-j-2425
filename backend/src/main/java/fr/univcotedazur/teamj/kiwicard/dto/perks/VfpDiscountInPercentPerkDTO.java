package fr.univcotedazur.teamj.kiwicard.dto.perks;

import fr.univcotedazur.teamj.kiwicard.mappers.PerkDTOVisitor;

public record VfpDiscountInPercentPerkDTO(Long perkId, double discountRate, int startHour,
                                          int endHour) implements IPerkDTO {
    @Override
    public String toString() {
        return discountRate + "% discount for all VFPs when booking between " + startHour + "h and " + endHour + "h";
    }

    @Override
    public <T> T accept(PerkDTOVisitor<T> visitor) {
        return visitor.fromDTO(this);
    }
}
