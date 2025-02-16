package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.dto.ItemDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PartnerCreationDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PartnerDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.entities.Partner;
import fr.univcotedazur.teamj.kiwicard.entities.perks.AbstractPerk;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.partner.IPartnerManager;
import fr.univcotedazur.teamj.kiwicard.repositories.IItemRepository;
import fr.univcotedazur.teamj.kiwicard.repositories.IPartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PartnerCatalog implements IPartnerManager {

    private final IPartnerRepository partnerRepository;
    private final IItemRepository itemRepository;

    @Autowired
    public PartnerCatalog(IPartnerRepository partnerRepository, IItemRepository itemRepository) {
        this.partnerRepository = partnerRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public PartnerDTO createPartner(PartnerCreationDTO partnerToCreate) {
        Partner partner = new Partner(partnerToCreate);
        partnerRepository.save(partner);
        return new PartnerDTO(partner);
    }

    @Override
    public PartnerDTO findPartnerById(long partnerId) throws UnknownPartnerIdException {
        return partnerRepository.findById(partnerId)
                .map(PartnerDTO::new)
                .orElseThrow(() -> new UnknownPartnerIdException("Partner with id " + partnerId + " not found"));
    }

    @Override
    public List<PartnerDTO> findAllPartner() {
        return partnerRepository.findAll().stream().map(PartnerDTO::new).toList();
    }

    @Override
    public void addItemToPartnerCatalog(ItemDTO itemDTO) {
        Item item = new Item(itemDTO);
        itemRepository.save(item);
    }

    @Override
    public boolean removeItemFromPartnerCatalog(long partnerId, long itemId) throws UnknownPartnerIdException {
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new UnknownPartnerIdException("Partner with id " + partnerId + " not found"));
        return partner.getItemList().removeIf(item -> item.getItemId().equals(itemId));
    }

    @Override
    @Transactional
    public List<Item> findAllPartnerItems(long partnerId) throws UnknownPartnerIdException {
        return partnerRepository.findById(partnerId)
                .map(Partner::getItemList)
                .orElseThrow(() -> new UnknownPartnerIdException("Partner with id " + partnerId + " not found"));
    }
    @Override
    public boolean removePerkFromPartner(long partnerId, long perkId) throws UnknownPartnerIdException {
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new UnknownPartnerIdException("Partner with id " + partnerId + " not found"));
        return partner.getPerkList().removeIf(perk -> perk.getPerkId().equals(perkId));
    }

    @Override
    public List<AbstractPerk> findAllPartnerPerks(long partnerId) throws UnknownPartnerIdException {
        return partnerRepository.findById(partnerId)
                .map(Partner::getPerkList)
                .orElseThrow(() -> new UnknownPartnerIdException("Partner with id " + partnerId + " not found"));
    }
}

