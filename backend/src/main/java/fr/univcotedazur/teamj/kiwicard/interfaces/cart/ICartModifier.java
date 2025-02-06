package fr.univcotedazur.teamj.kiwicard.interfaces.cart;

import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PurchaseDTO;
import fr.univcotedazur.teamj.kiwicard.dto.UsedPerkDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;

/**
 * Modification de panier lros de l'application d'un avantage
 */
public interface ICartModifier {
    CartDTO updateCart(String cartOwnerEmail, CartDTO newCart, UsedPerkDTO usedPerk) throws UnknownCustomerEmailException;
    PurchaseDTO validateCart(String cartOwnerEmail) throws UnknownCustomerEmailException;
}
