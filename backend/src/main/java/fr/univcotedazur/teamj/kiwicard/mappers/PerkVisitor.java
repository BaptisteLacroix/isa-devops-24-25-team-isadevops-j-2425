package fr.univcotedazur.teamj.kiwicard.mappers;

import fr.univcotedazur.teamj.kiwicard.entities.perks.NPurchasedMGiftedPerk;
import fr.univcotedazur.teamj.kiwicard.entities.perks.TimedDiscountInPercentPerk;
import fr.univcotedazur.teamj.kiwicard.entities.perks.VfpDiscountInPercentPerk;

public interface PerkVisitor<T> {
    T toDTO(NPurchasedMGiftedPerk perk);
    T toDTO(TimedDiscountInPercentPerk perk);
    T toDTO(VfpDiscountInPercentPerk perk);
}
