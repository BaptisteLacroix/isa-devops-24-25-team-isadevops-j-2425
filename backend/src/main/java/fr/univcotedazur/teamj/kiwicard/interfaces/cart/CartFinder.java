package fr.univcotedazur.teamj.kiwicard.interfaces.cart;

import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;

import java.util.Optional;

/**
 * Recherche et récupération de panier
 */
public interface CartFinder {
    Optional<CartDTO> findCustomerCart(String cartOwnerEmail) throws UnknownCustomerEmailException;
}
