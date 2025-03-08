package fr.univcotedazur.teamj.kiwicard.interfaces.customer;

import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;

/**
 * Permet d'enregistrer le panier d'un client temporairement (jusqu'à ce qu'il soit payer)
 */
public interface ICustomerCartSaver {
    Customer setCart(String customerEMail, Cart cart) throws UnknownCustomerEmailException;
    void emptyCart(String customerEMail) throws UnknownCustomerEmailException;
}
