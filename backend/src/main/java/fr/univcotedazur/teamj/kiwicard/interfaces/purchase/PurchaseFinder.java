package fr.univcotedazur.teamj.kiwicard.interfaces.purchase;

import fr.univcotedazur.teamj.kiwicard.dto.PurchaseDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;

import java.util.List;
import java.util.Optional;

/**
 * Recherche et récupération d'achat lors de l'application d'avantage
 */
public interface PurchaseFinder {
    Optional<PurchaseDTO> findPurchaseById(long purchaseId) throws UnknownPartnerIdException;
    List<PurchaseDTO> findPurchasesByCustomerAndPartner(String customerEmail, long partnerId) throws UnknownCustomerEmailException, UnknownPartnerIdException;
}
