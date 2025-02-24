package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.dto.ItemDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PartnerCreationDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PartnerDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PerkDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.entities.Partner;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownItemIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.partner.IPartnerManager;
import fr.univcotedazur.teamj.kiwicard.repositories.IItemRepository;
import fr.univcotedazur.teamj.kiwicard.repositories.IPartnerRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    public PartnerDTO createPartner(@NotNull PartnerCreationDTO partnerToCreate) {
        Partner partner = new Partner(partnerToCreate);
        partnerRepository.save(partner);
        return new PartnerDTO(partner);
    }

    @Override
    public PartnerDTO findPartnerById(long partnerId) throws UnknownPartnerIdException {
        return partnerRepository.findById(partnerId)
                .map(PartnerDTO::new)
                .orElseThrow(() -> new UnknownPartnerIdException(partnerId));
    }

    @Override
    public List<PartnerDTO> findAllPartner() {
        return partnerRepository.findAll().stream().map(PartnerDTO::new).toList();
    }

    @Override
    @Transactional
    public void addItemToPartnerCatalog(long partnerId, @NotNull ItemDTO itemDTO) throws UnknownPartnerIdException {
        Optional<Partner> optionalPartner = partnerRepository.findById(partnerId);
        Partner partner = partnerRepository.findById(partnerId).orElseThrow(() -> new UnknownPartnerIdException(partnerId));

        Item item = new Item(itemDTO);
        itemRepository.save(item);
        partner.addItem(item);
    }

    @Override
    @Transactional
    public boolean removeItemFromPartnerCatalog(long partnerId, long itemId) throws UnknownPartnerIdException, UnknownItemIdException {
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new UnknownPartnerIdException(partnerId));
        boolean removed = partner.getItemList().removeIf(item -> item.getItemId().equals(itemId));
        if (!removed) {
            throw new UnknownItemIdException(itemId, partner.getName());
        }
        return true;
    }

    @Override
    @Transactional
    public List<Item> findAllPartnerItems(long partnerId) throws UnknownPartnerIdException {
        return partnerRepository.findById(partnerId)
                .map(Partner::getItemList)
                .orElseThrow(() -> new UnknownPartnerIdException(partnerId));
    }

    @Override
    public List<PerkDTO> findAllPartnerPerks(long partnerId) throws UnknownPartnerIdException {
        return partnerRepository.findById(partnerId)
                .map(Partner::getPerkList)
                .map(perks -> perks.stream().map(PerkDTO::new).toList())
                .orElseThrow(() -> new UnknownPartnerIdException(partnerId));
    }

    @Override
    public void udpatePartner(long partnerId, PartnerDTO newPartner) throws UnknownPartnerIdException {

    }

    @Override
    public void deletePartner(long partnerId) throws UnknownPartnerIdException {

    }
}

