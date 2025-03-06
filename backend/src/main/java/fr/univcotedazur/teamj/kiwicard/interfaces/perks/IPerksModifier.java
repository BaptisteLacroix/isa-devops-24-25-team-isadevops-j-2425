package fr.univcotedazur.teamj.kiwicard.interfaces.perks;

import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPerkIdException;

/**
 * Modification ou suppression d'un avatange
 */
public interface IPerksModifier {
    void updatePerk(long perkId, IPerkDTO newPerk) throws UnknownPerkIdException;
    void deletePerk(long perkId) throws UnknownPerkIdException;
}
