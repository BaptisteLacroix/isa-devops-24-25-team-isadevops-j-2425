package fr.univcotedazur.teamj.kiwicard.interfaces.cart;

import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;

import java.util.Set;

/**
 * Création de panier lors d'un achat chez un partenaire
 */
public interface ICartCreator {
    CartDTO createCart(String customerEmail, Set<Long> itemIds) throws UnknownCustomerEmailException;
}
