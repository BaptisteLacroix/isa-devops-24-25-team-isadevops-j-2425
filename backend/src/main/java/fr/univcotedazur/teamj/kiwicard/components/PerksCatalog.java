package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import fr.univcotedazur.teamj.kiwicard.entities.perks.AbstractPerk;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPerkIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.partner.IPerkManager;
import fr.univcotedazur.teamj.kiwicard.mappers.PerkMapper;
import fr.univcotedazur.teamj.kiwicard.repositories.IPerkRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PerksCatalog implements IPerkManager {
    public static final String NOT_IMPLEMENTED_YET = "Not implemented yet";
    private final IPerkRepository perksRepository;
    public PerksCatalog(IPerkRepository perksRepository) {
        this.perksRepository = perksRepository;
    }

    @Override
    public IPerkDTO createPerk(IPerkDTO perkToCreate) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    @Override
    public IPerkDTO findPerkById(long perkId) throws UnknownPerkIdException {
        AbstractPerk perk= perksRepository.findById(perkId).orElseThrow(() -> new UnknownPerkIdException(perkId));
        return PerkMapper.toDTO(perk);
    }

    @Override
    public List<IPerkDTO> findAllPerks() {
        return perksRepository.findAll().stream().map(PerkMapper::toDTO).toList();
    }

    @Override
    public void updatePerk(long perkId, IPerkDTO newPerk) throws UnknownPerkIdException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    @Override
    public void deletePerk(long perkId) throws UnknownPerkIdException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }
}
