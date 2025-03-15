package fr.univcotedazur.teamj.kiwicard.interfaces.purchase;

import fr.univcotedazur.teamj.kiwicard.entities.Purchase;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;

/**
 * Création d'achat lors du règlement d'un panier
 */
public interface IPurchaseCreator {
    Purchase createPurchase(String customerEmail, Long  amount) throws UnknownCustomerEmailException;

}
