package fr.univcotedazur.teamj.kiwicard.dto.perks;


import fr.univcotedazur.teamj.kiwicard.mappers.PerkDTOVisitor;

public record VfpDiscountInPercentPerkDTO(Long perkId, double discountRate) implements IPerkDTO {
    @Override
    public String toString() {
        return  discountRate + "% discount for all VFPs";
    }

    @Override
    public <T> T accept(PerkDTOVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
