package fr.univcotedazur.teamj.kiwicard.interfaces.cart;

import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CartItemDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PurchaseDTO;
import fr.univcotedazur.teamj.kiwicard.dto.UsedPerkDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownItemIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;

/**
 * Modification de panier lors de l'application d'un avantage
 */
public interface ICartModifier {
    /**
     * Adds a specified item to the customer's shopping cart. This method validates that the customer,
     * item, and item-partner relationship are valid before adding the item to the cart. If any of the
     * checks fail, an appropriate exception is thrown.
     *
     * @param cartOwnerEmail The email address of the customer whose cart the item will be added to.
     * @param cartItemDTO    A CartItemDTO representing the item to be added, including the item ID,
     *                       quantity, and time range.
     * @return A CartDTO representing the updated shopping cart after the item has been added.
     * @throws UnknownCustomerEmailException If no customer is found with the given email.
     * @throws UnknownItemIdException        If the specified item does not exist in the item repository
     *                                       or is not part of the partner's catalog.
     * @throws UnknownPartnerIdException     If no partner exists for the given cart or if the item does
     *                                       not belong to the partner's catalog.
     */
    CartDTO addItemToCart(String cartOwnerEmail, CartItemDTO cartItemDTO) throws UnknownCustomerEmailException, UnknownItemIdException, UnknownPartnerIdException;

    /**
     * Removes a specified item from the customer's shopping cart. The method first validates that the customer exists,
     * then removes the item from the customer's cart if it exists. If the customer is not found, an exception is thrown.
     *
     * @param cartOwnerEmail The email address of the customer whose cart the item will be removed from.
     * @param cartItemDTO    A CartItemDTO representing the item to be removed, including the item ID.
     * @return A CartDTO representing the updated shopping cart after the item has been removed.
     * @throws UnknownCustomerEmailException If no customer is found with the given email.
     */
    CartDTO removeItemFromCart(String cartOwnerEmail, CartItemDTO cartItemDTO) throws UnknownCustomerEmailException;
    /**
     * Validates a customer's shopping cart and processes a payment request to the external payment service.
     * This method checks that the customer exists, retrieves the cart associated with the customer,
     * and makes a payment request to an external service. It returns a PurchaseDTO that includes the customer's
     * cart and the payment details.
     *
     * @param cartOwnerEmail The email address of the customer whose cart is being validated.
     * @return A PurchaseDTO containing the customer's email, the validated cart, and the
     * payment details from the external service.
     * @throws UnknownCustomerEmailException       If no customer is found with the given email.
     * @throws UnreachableExternalServiceException If there is an issue contacting or processing the payment
     *                                             with the external service.
     */
    PurchaseDTO validateCart(String cartOwnerEmail) throws UnknownCustomerEmailException, UnreachableExternalServiceException;
}
