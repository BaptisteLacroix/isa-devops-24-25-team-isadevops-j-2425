package fr.univcotedazur.teamj.kiwicard.interfaces.purchase;

import fr.univcotedazur.teamj.kiwicard.dto.PurchaseHistoryDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPurchaseIdException;

import java.util.List;

/**
 * Recherche et récupération d'achat lors de l'application d'avantage
 */
public interface IPurchaseFinder {
    PurchaseHistoryDTO findPurchaseById(long purchaseId) throws UnknownPartnerIdException, UnknownPurchaseIdException;
    List<PurchaseHistoryDTO> findPurchasesByCustomerAndPartner(String customerEmail, long partnerId) throws UnknownCustomerEmailException, UnknownPartnerIdException;
    List<PurchaseHistoryDTO> findPurchasesByPartnerId(long partnerId) throws UnknownPartnerIdException;
    List<PurchaseHistoryDTO> findPurchasesByCustomerEmail(String customerEmail) throws UnknownCustomerEmailException;
    List<PurchaseHistoryDTO> findPurchasesByCustomerEmail(String customerEmail, int limit) throws UnknownCustomerEmailException;
    List<PurchaseHistoryDTO> findPurchasesByPartnerId(long partnerId, int limit) throws UnknownPartnerIdException;
}
