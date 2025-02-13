package fr.univcotedazur.teamj.kiwicard.interfaces.partner;

import fr.univcotedazur.teamj.kiwicard.dto.PartnerDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;

import java.util.List;

public interface IPartnerManager {
    PartnerDTO createPartner(PartnerDTO partnerToCreate);
    PartnerDTO findPartnerById(long partnerId) throws UnknownPartnerIdException;
    List<PartnerDTO> findAllPartner();
    void udpatePartner(long partnerId, PartnerDTO newPartner) throws UnknownPartnerIdException;
    void deletePartner(long partnerId) throws UnknownPartnerIdException;
}
