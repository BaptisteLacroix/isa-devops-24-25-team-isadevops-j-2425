package fr.univcotedazur.teamj.kiwicard.interfaces.purchase;

import fr.univcotedazur.teamj.kiwicard.dto.PaymentDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PurchaseDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCartIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPaymentIdException;

/**
 * Création d'achat lors du règlement d'un panier
 */
public interface IPurchaseCreator {
    PurchaseDTO createPurchase(String customerEmail, Long  amount) throws UnknownCustomerEmailException, UnknownPaymentIdException;

}
