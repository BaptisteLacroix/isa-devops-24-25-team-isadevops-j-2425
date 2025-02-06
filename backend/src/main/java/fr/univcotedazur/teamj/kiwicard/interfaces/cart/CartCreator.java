package fr.univcotedazur.teamj.kiwicard.interfaces.cart;

import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.dto.ItemDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;

import java.util.List;

/**
 * Création de panier lors d'un achat chez un partenaire
 */
public interface CartCreator {
    CartDTO createCart(String customerEmail, List<ItemDTO> items) throws UnknownCustomerEmailException;
}
