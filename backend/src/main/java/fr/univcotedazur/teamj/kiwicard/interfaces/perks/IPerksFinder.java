package fr.univcotedazur.teamj.kiwicard.interfaces.perks;

import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPerkIdException;

import java.util.List;
import java.util.Map;

/**
 * Recherche et récupération d'avantage
 */
public interface IPerksFinder {
    Map<String, Long> aggregatePartnerPerksUsageByType(long partnerId) throws UnknownPartnerIdException;
    IPerkDTO findPerkById(long perkId) throws UnknownPerkIdException;
    List<IPerkDTO> findAllPerks();
}
