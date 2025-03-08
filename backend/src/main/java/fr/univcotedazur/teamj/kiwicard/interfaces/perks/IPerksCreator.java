package fr.univcotedazur.teamj.kiwicard.interfaces.perks;

import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;

/**
 * Permet aux partenaires de créer des avantages
 */
public interface IPerksCreator {
    IPerkDTO createPerk(IPerkDTO perkToCreate);
}
