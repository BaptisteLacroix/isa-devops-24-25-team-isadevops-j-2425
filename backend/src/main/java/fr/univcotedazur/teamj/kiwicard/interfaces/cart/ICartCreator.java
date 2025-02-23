package fr.univcotedazur.teamj.kiwicard.interfaces.cart;

import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;

import java.util.List;

/**
 * Création de panier lors d'un achat chez un partenaire
 */
public interface ICartCreator {
    CartDTO createCart(String customerEmail, List<Long> itemIds) throws UnknownCustomerEmailException;
}
