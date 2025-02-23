package fr.univcotedazur.teamj.kiwicard.dto.perks;

import com.fasterxml.jackson.annotation.JsonGetter;

public sealed interface IPerkDTO permits NPurchasedMGiftedPerkDTO, TimedDiscountInPercentPerkDTO, VfpDiscountInPercentPerkDTO {
    Long perkId();
    @JsonGetter("description")
    default  String getDescription() {
        return this.toString();
    }
}

