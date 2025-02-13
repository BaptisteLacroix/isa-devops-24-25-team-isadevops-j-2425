package fr.univcotedazur.teamj.kiwicard.interfaces.purchase;

import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PaymentDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PurchaseDTO;

/**
 * Création d'achat lors du règlement d'un panier
 */
public interface IPurchaseCreator {
    PurchaseDTO createPurchase(CartDTO cart, PaymentDTO payment);
}
