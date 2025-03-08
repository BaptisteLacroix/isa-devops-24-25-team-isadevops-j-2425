package fr.univcotedazur.teamj.kiwicard.interfaces.perks;

import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPerkIdException;

import java.util.List;

/**
 * Recherche et récupération d'avantage
 */
public interface IPerksFinder {
    IPerkDTO findPerkById(long perkId) throws UnknownPerkIdException;
    List<IPerkDTO> findAllPerks();
}
