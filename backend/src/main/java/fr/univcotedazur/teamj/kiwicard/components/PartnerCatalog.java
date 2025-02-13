package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.dto.PartnerDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownItemIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.partner.IItemManager;
import fr.univcotedazur.teamj.kiwicard.interfaces.partner.IPartnerManager;
import fr.univcotedazur.teamj.kiwicard.repositories.IItemRepository;
import fr.univcotedazur.teamj.kiwicard.repositories.IPartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartnerCatalog implements IPartnerManager, IItemManager {

    private final IPartnerRepository partnerRepository;
    private final IItemRepository itemRepository;

    @Autowired
    public PartnerCatalog(IPartnerRepository partnerRepository, IItemRepository itemRepository) {
        this.partnerRepository = partnerRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public PartnerDTO createPartner(PartnerDTO partnerToCreate) {
        return null;
    }

    @Override
    public PartnerDTO findPartnerById(long partnerId) throws UnknownPartnerIdException {
        return null;
    }

    @Override
    public List<PartnerDTO> findAllPartner() {
        return List.of();
    }

    @Override
    public void udpatePartner(long partnerId, PartnerDTO newPartner) throws UnknownPartnerIdException {

    }

    @Override
    public void deletePartner(long partnerId) throws UnknownPartnerIdException {

    }

    @Override
    public Item createItem(Item ItemToCreate) {
        return null;
    }

    @Override
    public Item findItemById(long itemId) throws UnknownItemIdException {
        return null;
    }

    @Override
    public List<Item> findAllItem() {
        return List.of();
    }

    @Override
    public void udpateItem(long itemId, Item newItem) throws UnknownItemIdException {

    }

    @Override
    public void deleteItem(long itemId) throws UnknownItemIdException {

    }
}
