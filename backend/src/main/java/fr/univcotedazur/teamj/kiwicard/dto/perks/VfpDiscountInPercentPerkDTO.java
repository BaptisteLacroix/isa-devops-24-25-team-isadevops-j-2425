package fr.univcotedazur.teamj.kiwicard.dto.perks;


public record VfpDiscountInPercentPerkDTO(Long perkId, double discountRate) implements IPerkDTO {
    @Override
    public String toString() {
        return  discountRate + "% discount for all VFPs";
    }
}
