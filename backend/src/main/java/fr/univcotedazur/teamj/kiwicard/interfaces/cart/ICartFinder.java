package fr.univcotedazur.teamj.kiwicard.interfaces.cart;

import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;

import java.util.Optional;

/**
 * Recherche et récupération de panier
 */
public interface ICartFinder {
    /**
     * Retrieves the shopping cart of a customer by their email address. If the customer exists,
     * their cart is returned as a CartDTO. If the customer is not found, an exception is thrown.
     *
     * @param cartOwnerEmail The email address of the customer whose cart is to be retrieved.
     * @return An Optional containing a CartDTO representing the customer's cart,
     * or an empty Optional if the customer has no cart (though this is unlikely).
     * @throws UnknownCustomerEmailException If no customer is found with the given email.
     */
    Optional<CartDTO> findCustomerCart(String cartOwnerEmail) throws UnknownCustomerEmailException;
}
