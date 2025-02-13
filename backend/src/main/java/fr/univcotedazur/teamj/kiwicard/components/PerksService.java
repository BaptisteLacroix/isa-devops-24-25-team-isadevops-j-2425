package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.dto.PerkDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCartIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPerkIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.perks.IPerksConsumer;

import java.util.List;

public class PerksService implements IPerksConsumer {
    @Override
    public boolean applyPerk(long perkId, String cartOwnerEmail) throws UnknownPerkIdException, UnknownCustomerEmailException {
        return false;
    }

    @Override
    public List<PerkDTO> findConsumablePerksForConsumerAtPartner(String consumerEmail, long partnerId) throws UnknownCartIdException, UnknownPartnerIdException {
        return List.of();
    }
}
