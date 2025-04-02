package fr.univcotedazur.teamj.kiwicard.interfaces.perks;

import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.InapplicablePerkException;
import fr.univcotedazur.teamj.kiwicard.exceptions.NoCartException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPerkIdException;

import java.util.List;

/**
 * Application d'un avantage sur un panier
 */
public interface IPerksConsumer {
    CartDTO addPerkToApply(long perkId, String cartOwnerEmail) throws UnknownPerkIdException, UnknownCustomerEmailException, NoCartException, InapplicablePerkException;
    List<IPerkDTO> findConsumablePerksForConsumerAtPartner(String consumerEmail) throws UnknownCustomerEmailException, NoCartException;
}
