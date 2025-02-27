package fr.univcotedazur.teamj.kiwicard.interfaces.partner;

import fr.univcotedazur.teamj.kiwicard.dto.ItemDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PartnerCreationDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PartnerDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PerkDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownItemIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;

import java.util.List;

public interface IPartnerManager {
    PartnerDTO createPartner(PartnerCreationDTO partnerToCreate);
    PartnerDTO findPartnerById(long partnerId) throws UnknownPartnerIdException;
    List<PartnerDTO> findAllPartner();
    void addItemToPartnerCatalog(long partnerId, ItemDTO itemDTO) throws UnknownPartnerIdException;
    boolean removeItemFromPartnerCatalog(long partnerId, long itemId) throws UnknownPartnerIdException, UnknownItemIdException;
    List<Item> findAllPartnerItems(long partnerId) throws UnknownPartnerIdException;
    List<PerkDTO> findAllPartnerPerks(long partnerId) throws UnknownPartnerIdException;
}
