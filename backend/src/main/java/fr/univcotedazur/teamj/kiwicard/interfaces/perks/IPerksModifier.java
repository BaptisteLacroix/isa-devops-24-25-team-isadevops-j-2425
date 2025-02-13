package fr.univcotedazur.teamj.kiwicard.interfaces.perks;

import fr.univcotedazur.teamj.kiwicard.dto.PerkDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPerkIdException;

/**
 * Modification ou suppression d'un avatange
 */
public interface IPerksModifier {
    void udpatePerk(long perkId, PerkDTO newPerk) throws UnknownPerkIdException;
    void deletePerk(long perkId) throws UnknownPerkIdException;
}
