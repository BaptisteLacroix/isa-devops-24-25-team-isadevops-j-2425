package fr.univcotedazur.teamj.kiwicard.interfaces.purchase;

import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.entities.Purchase;

/**
 * Création d'achat lors du règlement d'un panier
 */
public interface IPurchaseCreator {
    Purchase createPurchase(Customer customer, double  amount);

}
