package fr.univcotedazur.teamj.kiwicard.mappers;

import fr.univcotedazur.teamj.kiwicard.entities.perks.NPurchasedMGiftedPerk;
import fr.univcotedazur.teamj.kiwicard.entities.perks.TimedDiscountInPercentPerk;
import fr.univcotedazur.teamj.kiwicard.entities.perks.VfpDiscountInPercentPerk;

public interface PerkVisitor<T> {
    T visit(NPurchasedMGiftedPerk perk);
    T visit(TimedDiscountInPercentPerk perk);
    T visit(VfpDiscountInPercentPerk perk);
}
