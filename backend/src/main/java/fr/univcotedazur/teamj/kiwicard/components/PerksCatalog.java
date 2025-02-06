package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.dto.PerkDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPerkIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.perks.IPerksCreator;
import fr.univcotedazur.teamj.kiwicard.interfaces.perks.IPerksFinder;
import fr.univcotedazur.teamj.kiwicard.interfaces.perks.IPerksModifier;

import java.util.List;
import java.util.Optional;

public class PerksCatalog implements IPerksCreator, IPerksFinder, IPerksModifier {
    @Override
    public PerkDTO createPerk(PerkDTO perkToCreate) {
        return null;
    }

    @Override
    public Optional<PerkDTO> findPerkById(long perkId) throws UnknownPerkIdException {
        return Optional.empty();
    }

    @Override
    public List<PerkDTO> findPerkByPartner(long partnerId) throws UnknownPartnerIdException {
        return List.of();
    }

    @Override
    public List<PerkDTO> findAllPerks() {
        return List.of();
    }

    @Override
    public void udpatePerk(long perkId, PerkDTO newPerk) throws UnknownPerkIdException {

    }

    @Override
    public void deletePerk(long perkId) throws UnknownPerkIdException {

    }
}
