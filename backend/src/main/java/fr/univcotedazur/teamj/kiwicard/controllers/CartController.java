package fr.univcotedazur.teamj.kiwicard.controllers;

import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CartItemDTO;
import fr.univcotedazur.teamj.kiwicard.dto.ItemDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PerkDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PurchaseDTO;
import fr.univcotedazur.teamj.kiwicard.dto.UsedPerkDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownItemIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
import fr.univcotedazur.teamj.kiwicard.interfaces.cart.ICartCreator;
import fr.univcotedazur.teamj.kiwicard.interfaces.cart.ICartFinder;
import fr.univcotedazur.teamj.kiwicard.interfaces.cart.ICartModifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private final ICartCreator creator;
    private final ICartModifier modifier;
    private final ICartFinder finder;

    /**
     * Constructs a new CartController with the provided dependencies.
     *
     * @param creator The service responsible for creating shopping carts.
     * @param modifier The service responsible for modifying shopping carts (e.g., adding/removing items).
     * @param finder The service responsible for finding and retrieving shopping carts.
     */
    @Autowired
    public CartController(ICartCreator creator, ICartModifier modifier, ICartFinder finder) {
        this.creator = creator;
        this.modifier = modifier;
        this.finder = finder;
    }

    /**
     * Creates a new shopping cart for a customer with the specified partner and items.
     *
     * @param customerEmail The email address of the customer for whom the cart is being created.
     * @param partnerId The ID of the partner whose items will be included in the cart.
     * @param cartItemDTOS A list of CartItemDTOs representing the items to be added to the cart.
     * @return A ResponseEntity containing the created CartDTO with HTTP status 201 (Created).
     * @throws UnknownCustomerEmailException If no customer is found with the given email.
     * @throws UnknownPartnerIdException If no partner is found with the given ID.
     * @throws UnknownItemIdException If any of the items in the cart are invalid or do not belong to the partner.
     */
    @PostMapping(path = "/{customerEmail}/{partnerId}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<CartDTO> createCart(@PathVariable String customerEmail, @PathVariable Long partnerId, @RequestBody List<CartItemDTO> cartItemDTOS) throws UnknownCustomerEmailException, UnknownPartnerIdException, UnknownItemIdException {
        return ResponseEntity.created(null)
                .body(creator.createCart(customerEmail, partnerId, cartItemDTOS));
    }

    /**
     * Adds an item to an existing shopping cart.
     *
     * @param customerEmail The email address of the customer whose cart will be updated.
     * @param cartItemDTO The CartItemDTO containing the item details to be added.
     * @return A ResponseEntity containing the updated CartDTO with HTTP status 201 (Created).
     * @throws UnknownCustomerEmailException If no customer is found with the given email.
     * @throws UnknownPartnerIdException If the item does not belong to the customer's current partner.
     * @throws UnknownItemIdException If the specified item does not exist in the item repository.
     */
    @PutMapping(path = "/{customerEmail}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<CartDTO> addItemToCart(@PathVariable String customerEmail, @RequestBody CartItemDTO cartItemDTO) throws UnknownCustomerEmailException, UnknownPartnerIdException, UnknownItemIdException {
        return ResponseEntity.created(null)
                .body(modifier.addItemToCart(customerEmail, cartItemDTO));
    }

    /**
     * Removes an item from a customer's shopping cart.
     *
     * @param customerEmail The email address of the customer whose cart will be updated.
     * @param cartItemDTO The CartItemDTO containing the item details to be removed.
     * @return A ResponseEntity containing the updated CartDTO with HTTP status 201 (Created).
     * @throws UnknownCustomerEmailException If no customer is found with the given email.
     */
    @DeleteMapping(path = "/{customerEmail}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<CartDTO> removeItemFromCart(@PathVariable String customerEmail, @RequestBody CartItemDTO cartItemDTO) throws UnknownCustomerEmailException {
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
    @PostMapping(path =  "/{customerEmail}/validate", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<PurchaseDTO> validateCart(@PathVariable String customerEmail) throws UnknownCustomerEmailException, UnreachableExternalServiceException {
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
                .body(finder.findCustomerCart(customerEmail).orElseThrow(UnknownCustomerEmailException::new));
    }
}

