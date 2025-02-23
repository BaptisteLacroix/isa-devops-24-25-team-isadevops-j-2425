package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCartIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPerkIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.partner.IPartnerManager;
import fr.univcotedazur.teamj.kiwicard.interfaces.perks.IPerksConsumer;
import fr.univcotedazur.teamj.kiwicard.repositories.IPerkRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PerksService implements IPerksConsumer {
    private final IPerkRepository perksRepository;
    private final IPartnerManager partnerManager;

    public PerksService(IPerkRepository perksRepository, IPartnerManager partnerManager) {
        this.perksRepository = perksRepository;
        this.partnerManager = partnerManager;
    }

    @Override
    public boolean applyPerk(long perkId, String cartOwnerEmail) throws UnknownPerkIdException, UnknownCustomerEmailException {
        return false;
    }

    @Override
    public List<IPerkDTO> findConsumablePerksForConsumerAtPartner(String consumerEmail, long partnerId) throws UnknownCartIdException, UnknownPartnerIdException {
        return List.of();
    }
}
