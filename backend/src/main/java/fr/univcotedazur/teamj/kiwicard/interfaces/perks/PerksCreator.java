package fr.univcotedazur.teamj.kiwicard.interfaces.perks;

import fr.univcotedazur.teamj.kiwicard.dto.PerkDTO;

/**
 * Permet aux partenaires de créer des avantages
 */
public interface PerksCreator{
    PerkDTO createPerk(PerkDTO perkToCreate);
}
