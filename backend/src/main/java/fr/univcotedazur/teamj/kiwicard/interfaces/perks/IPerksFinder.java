package fr.univcotedazur.teamj.kiwicard.interfaces.perks;

import fr.univcotedazur.teamj.kiwicard.dto.PerkDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPerkIdException;

import java.util.List;
import java.util.Optional;

/**
 * Recherche et récupération d'avantage
 */
public interface IPerksFinder {
    Optional<PerkDTO> findPerkById(long perkId) throws UnknownPerkIdException;
    List<PerkDTO> findPerkByPartner(long partnerId) throws UnknownPartnerIdException;
    List<PerkDTO> findAllPerks();
}
