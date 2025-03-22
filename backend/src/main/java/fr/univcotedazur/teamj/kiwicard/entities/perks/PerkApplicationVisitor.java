package fr.univcotedazur.teamj.kiwicard.entities.perks;

import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.exceptions.ClosedTimeException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;

public interface PerkApplicationVisitor {
    boolean visit(VfpDiscountInPercentPerk perk, Customer customer) throws ClosedTimeException, UnreachableExternalServiceException;

    boolean visit(TimedDiscountInPercentPerk perk, Customer customer);

    boolean visit(NPurchasedMGiftedPerk perk, Customer customer);
}

