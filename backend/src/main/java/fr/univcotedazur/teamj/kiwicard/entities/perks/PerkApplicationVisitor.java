package fr.univcotedazur.teamj.kiwicard.entities.perks;

import fr.univcotedazur.teamj.kiwicard.exceptions.ClosedTimeException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;

public interface PerkApplicationVisitor {
    boolean visit(VfpDiscountInPercentPerk perk) throws ClosedTimeException, UnreachableExternalServiceException;

    boolean visit(TimedDiscountInPercentPerk perk);

    boolean visit(NPurchasedMGiftedPerk perk);
}

