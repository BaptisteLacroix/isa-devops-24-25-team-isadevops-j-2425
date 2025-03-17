package fr.univcotedazur.teamj.kiwicard.interfaces.purchase;

import fr.univcotedazur.teamj.kiwicard.entities.Purchase;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPurchaseIdException;

import java.util.List;
import java.util.Optional;

/**
 * Recherche et récupération d'achat lors de l'application d'avantage
 */
public interface IPurchaseFinder {
    Purchase findPurchaseById(long purchaseId) throws UnknownPartnerIdException, UnknownPurchaseIdException;
    List<Purchase> findPurchasesByCustomerAndPartner(String customerEmail, long partnerId) throws UnknownCustomerEmailException, UnknownPartnerIdException;
    List<Purchase> findPurchasesByPartnerId(long partnerId) throws UnknownPartnerIdException;
    List<Purchase> findPurchasesByCutomerEmail(String customerEmail) throws UnknownCustomerEmailException;
    List<Purchase> findPurchasesByCutomerEmail(String customerEmail, int limit) throws UnknownCustomerEmailException;
    List<Purchase> findPurchasesByPartnerId(long partnerId, int limit) throws UnknownPartnerIdException;
}
