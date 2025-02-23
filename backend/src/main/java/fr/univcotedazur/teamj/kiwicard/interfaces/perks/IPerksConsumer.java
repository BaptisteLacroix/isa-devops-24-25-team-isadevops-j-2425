package fr.univcotedazur.teamj.kiwicard.interfaces.perks;

import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCartIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPerkIdException;

import java.util.List;

/**
 * Application d'un avantage sur un panier
 */
public interface IPerksConsumer {
    boolean applyPerk(long perkId, String cartOwnerEmail) throws UnknownPerkIdException, UnknownCustomerEmailException;
    List<IPerkDTO> findConsumablePerksForConsumerAtPartner(String consumerEmail, long partnerId) throws UnknownCartIdException, UnknownPartnerIdException;
}
