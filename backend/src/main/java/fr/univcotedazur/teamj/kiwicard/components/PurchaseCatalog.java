package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PaymentDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PurchaseDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.purchase.PurchaseConsumer;
import fr.univcotedazur.teamj.kiwicard.interfaces.purchase.PurchaseCreator;
import fr.univcotedazur.teamj.kiwicard.interfaces.purchase.PurchaseFinder;

import java.util.List;
import java.util.Optional;

public class PurchaseCatalog implements PurchaseConsumer, PurchaseCreator, PurchaseFinder {

    @Override
    public void consumeNLastPurchaseoOfCustomer(int nbPurchasesToConsume, String customerEmail) throws UnknownCustomerEmailException {

    }

    @Override
    public void consumeNLastPurchaseOfCustomerInPartner(int nbPurchasesToConsume, String customerEmail, long partnerId) {

    }

    @Override
    public void consumeNLastItemsOfCustomerInPartner(long itemId, int nbItemsConsumed, String customerEmail) throws UnknownCustomerEmailException {

    }

    @Override
    public PurchaseDTO createPurchase(CartDTO cart, PaymentDTO payment) {
        return null;
    }

    @Override
    public Optional<PurchaseDTO> findPurchaseById(long purchaseId) throws UnknownPartnerIdException {
        return Optional.empty();
    }

    @Override
    public List<PurchaseDTO> findPurchasesByCustomerAndPartner(String customerEmail, long partnerId) throws UnknownCustomerEmailException, UnknownPartnerIdException {
        return List.of();
    }
}
