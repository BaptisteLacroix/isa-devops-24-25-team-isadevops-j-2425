package fr.univcotedazur.teamj.kiwicard.controllers;

import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CartItemDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PurchaseDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.*;
import fr.univcotedazur.teamj.kiwicard.interfaces.cart.ICartFinder;
import fr.univcotedazur.teamj.kiwicard.interfaces.cart.ICartModifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Controller that handles HTTP requests related to customer shopping carts.
 * This controller allows for creating, modifying, and retrieving shopping carts,
 * including adding and removing items, validating carts, and retrieving cart details.
 */
@RestController
@RequestMapping(path = CartController.CART_URI, produces = APPLICATION_JSON_VALUE)
public class CartController {

    public static final String CART_URI = "/cart";
    private final ICartModifier modifier;
    private final ICartFinder finder;

    /**
     * Constructs a new CartController with the provided dependencies.
     *
     * @param modifier The service responsible for modifying shopping carts (e.g., adding/removing items).
     * @param finder   The service responsible for finding and retrieving shopping carts.
     */
    @Autowired
    public CartController(ICartModifier modifier, ICartFinder finder) {
        this.modifier = modifier;
        this.finder = finder;
    }

    /**
     * Creates a new cart or adds an item to an existing cart for the specified customer.
     * If the customer already has a cart, the item will be added to the cart; otherwise, a new cart will be created.
     *
     * @param customerEmail The email address of the customer whose cart will be modified.
     * @param cartItemDTO   A CartItemDTO containing the details of the item to be added.
     * @return A ResponseEntity containing the updated CartDTO, representing the customer's cart.
     * @throws UnknownCustomerEmailException If no customer is found with the given email.
     * @throws UnknownPartnerIdException     If no partner is found for the item in the cart.
     * @throws UnknownItemIdException        If the item does not exist in the item repository.
     */
    @PutMapping(path = "/{customerEmail}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<CartDTO> createOrAddItemToCart(
            @PathVariable String customerEmail,
            @RequestBody CartItemDTO cartItemDTO
    ) throws UnknownCustomerEmailException, UnknownPartnerIdException, UnknownItemIdException, NoCartException {
        CartDTO existingCart = finder.findCustomerCart(customerEmail).orElse(null);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(modifier.addItemToCart(customerEmail, cartItemDTO, existingCart));
    }

    /**
     * Removes an item from a customer's shopping cart.
     *
     * @param customerEmail The email address of the customer whose cart will be updated.
     * @param cartItemDTO   The CartItemDTO containing the item details to be removed.
     * @return A ResponseEntity containing the updated CartDTO with HTTP status 201 (Created).
     * @throws UnknownCustomerEmailException If no customer is found with the given email.
     */
    @DeleteMapping(path = "/{customerEmail}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<CartDTO> removeItemFromCart(@PathVariable String customerEmail, @RequestBody CartItemDTO cartItemDTO) throws UnknownCustomerEmailException, NoCartException, EmptyCartException {
        return ResponseEntity.created(null)
                .body(modifier.removeItemFromCart(customerEmail, cartItemDTO));
    }

    /**
     * Validate the purchase of the items in the customer's cart.
     *
     * @param customerEmail The email address of the customer whose cart will be validated.
     * @return A ResponseEntity containing the PurchaseDTO representing the validated cart, with HTTP status 201 (Created).
     * @throws UnknownCustomerEmailException If no customer is found with the given email.
     */
    @PostMapping(path = "/{customerEmail}/validate", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<PurchaseDTO> validateCart(@PathVariable String customerEmail) throws UnknownCustomerEmailException, UnreachableExternalServiceException, EmptyCartException, NoCartException, ClosedTimeException {
        return ResponseEntity.created(null)
                .body(modifier.validateCart(customerEmail));
    }

    /**
     * Retrieves the shopping cart for a specific customer.
     *
     * @param customerEmail The email address of the customer whose cart will be retrieved.
     * @return A ResponseEntity containing the CartDTO representing the customer's cart, with HTTP status 200 (OK).
     * @throws UnknownCustomerEmailException If no customer is found with the given email.
     */
    @GetMapping(path = "/{customerEmail}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<CartDTO> getCart(@PathVariable String customerEmail) throws UnknownCustomerEmailException {
        return ResponseEntity.ok()
                .body(finder.findCustomerCart(customerEmail).orElseThrow());
    }
}

