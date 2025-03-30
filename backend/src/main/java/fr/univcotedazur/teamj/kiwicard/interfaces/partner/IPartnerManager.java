package fr.univcotedazur.teamj.kiwicard.interfaces.partner;

import fr.univcotedazur.teamj.kiwicard.dto.ItemCreationDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PartnerCreationDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PartnerDTO;
import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.entities.Partner;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownItemIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;

import java.util.List;

public interface IPartnerManager {
    PartnerDTO createPartner(PartnerCreationDTO partnerToCreate);
    Partner findPartnerById(long partnerId) throws UnknownPartnerIdException;
    List<PartnerDTO> findAllPartner();

    void addItemToPartnerCatalog(long partnerId, ItemCreationDTO itemDTO) throws UnknownPartnerIdException;
    boolean removeItemFromPartnerCatalog(long partnerId, long itemId) throws UnknownPartnerIdException, UnknownItemIdException;
    List<Item> findAllPartnerItems(long partnerId) throws UnknownPartnerIdException;
    List<IPerkDTO> findAllPartnerPerks(long partnerId) throws UnknownPartnerIdException;
}
