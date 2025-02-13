package fr.univcotedazur.teamj.kiwicard.interfaces.partner;

import fr.univcotedazur.teamj.kiwicard.dto.ItemDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PartnerDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;

import java.util.List;

public interface IPartnerManager {
    PartnerDTO createPartner(PartnerDTO partnerToCreate);
    PartnerDTO findPartnerById(long partnerId) throws UnknownPartnerIdException;
    List<PartnerDTO> findAllPartner();
    void addItemToPartnerCatalog(ItemDTO itemDTO);
    boolean removeItemFromPartnerCatalog(long partnerId, long itemId) throws UnknownPartnerIdException;
    List<Item> findAllPartnerItems(long partnerId) throws UnknownPartnerIdException;
}
