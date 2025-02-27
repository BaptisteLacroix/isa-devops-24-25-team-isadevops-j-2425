package fr.univcotedazur.teamj.kiwicard.interfaces.cart;

import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CartItemDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownItemIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;

import java.util.List;

/**
 * Création de panier lors d'un achat chez un partenaire
 */
public interface ICartCreator {
    /**
     * Creates a new shopping cart for a customer, containing the specified items from a partner's catalog.
     * The method checks if the customer, partner, and each item in the cart exist, and ensures that
     * each item belongs to the specified partner's catalog. If any checks fail, appropriate exceptions
     * are thrown.
     *
     * @param customerEmail The email address of the customer who is creating the cart.
     * @param partnerId     The ID of the partner providing the items in the cart.
     * @param cartItemDTOS  A list of CartItemDTOs representing the items to be added to the cart.
     *                      Each CartItemDTO contains the item ID, quantity, and the start and end times for the item.
     * @return A CartDTO representing the created shopping cart.
     * @throws UnknownCustomerEmailException If no customer is found with the given email.
     * @throws UnknownPartnerIdException     If no partner is found with the given ID.
     * @throws UnknownItemIdException        If any of the items in the cart do not exist in the item repository
     *                                       or do not belong to the specified partner's catalog.
     */
    CartDTO createCart(String customerEmail, Long partnerId, List<CartItemDTO> cartItemDTOS) throws UnknownCustomerEmailException, UnknownPartnerIdException, UnknownItemIdException;
}
