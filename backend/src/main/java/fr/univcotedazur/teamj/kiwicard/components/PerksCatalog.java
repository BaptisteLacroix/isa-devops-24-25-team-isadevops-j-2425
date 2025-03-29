package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.dto.PerkCountDTO;
import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import fr.univcotedazur.teamj.kiwicard.entities.perks.AbstractPerk;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPerkIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.partner.IPerkManager;
import fr.univcotedazur.teamj.kiwicard.mappers.PerkMapper;
import fr.univcotedazur.teamj.kiwicard.repositories.IPartnerRepository;
import fr.univcotedazur.teamj.kiwicard.repositories.IPerkRepository;
import fr.univcotedazur.teamj.kiwicard.repositories.IPurchaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PerksCatalog implements IPerkManager {
    public static final String NOT_IMPLEMENTED_YET = "Not implemented yet";
    private final IPerkRepository perksRepository;
    public PerksCatalog(IPerkRepository perksRepository) {
        this.perksRepository = perksRepository;
    }

    /**
     * Creates a new perk in the catalog.
     *
     * @param perkToCreate The DTO containing the information of the perk to create.
     * @return The DTO of the created perk.
     */
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

    /**
     * Updates the information of a perk in the catalog.
     *
     * @param perkId  The ID of the perk to update.
     * @param newPerk The DTO containing the new information of the perk.
     * @throws UnknownPerkIdException If the perk with the specified ID does not exist.
     */
    @Override
    public void updatePerk(long perkId, IPerkDTO newPerk) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    @Override
    public void deletePerk(long perkId) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }


    @Transactional
    @Override
    public Map<String, Long> aggregatePartnerPerksUsageByType(long partnerId) throws UnknownPartnerIdException {
        var entries = this.perksRepository
                .countByTypeForPartner(partnerId);
        return entries.stream()
                .collect(Collectors.toMap(
                        PerkCountDTO::getPerkType,
                        PerkCountDTO::getCount
                ));
    }
}
