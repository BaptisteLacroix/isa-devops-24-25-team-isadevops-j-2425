package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CartItemAddItemToCartDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CartItemDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PaymentDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PurchaseDTO;
import fr.univcotedazur.teamj.kiwicard.entities.*;
import fr.univcotedazur.teamj.kiwicard.exceptions.*;
import fr.univcotedazur.teamj.kiwicard.interfaces.IPayment;
import fr.univcotedazur.teamj.kiwicard.interfaces.cart.ICartFinder;
import fr.univcotedazur.teamj.kiwicard.interfaces.cart.ICartModifier;
import fr.univcotedazur.teamj.kiwicard.interfaces.partner.IPartnerManager;
import fr.univcotedazur.teamj.kiwicard.repositories.IItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CartService implements ICartModifier, ICartFinder {

    private final IItemRepository itemRepository;
    private final IPartnerManager partnerManager;
    private final IPayment payment;
    private final CustomerCatalog customerCatalog;

    @Autowired
    public CartService(IItemRepository itemRepository, IPartnerManager partnerManager, IPayment payment, CustomerCatalog customerCatalog) {
        this.itemRepository = itemRepository;
        this.partnerManager = partnerManager;
        this.payment = payment;
        this.customerCatalog = customerCatalog;
    }


    /**
     * Adds an item to the customer's cart. If the customer does not have a cart,
     * a new cart is created. The method validates the item and checks whether it belongs to
     * the correct partner's catalog before adding it to the cart.
     *
     * @param customerEmail The email address of the customer whose cart the item will be added to.
     * @param cartItemDTO   A CartItemDTO containing details of the item to be added.
     * @param cartDTO       An existing CartDTO representing the customer's current cart. If null, a new cart is created.
     * @return A CartDTO representing the updated shopping cart after the item has been added.
     * @throws UnknownCustomerEmailException If no customer is found with the given email.
     * @throws UnknownPartnerIdException     If no partner is found for the item in the cart.
     * @throws UnknownItemIdException        If the item does not exist in the item repository.
     * @throws NoCartException               If the customer does not have a cart.
     */
    @Override
    @Transactional
    public CartDTO addItemToCart(String customerEmail, CartItemAddItemToCartDTO cartItemDTO, CartDTO cartDTO) throws UnknownCustomerEmailException, UnknownPartnerIdException, UnknownItemIdException, NoCartException {
        // Found the customer in the bdd
        Customer customer = customerCatalog.findCustomerByEmail(customerEmail);
        Item item = itemRepository.findById(cartItemDTO.itemId()).orElseThrow(() -> new UnknownItemIdException(cartItemDTO.itemId()));
        if (cartDTO == null) {
            return createCart(cartItemDTO, customer, item);
        } else {
            return addItemToCart(cartItemDTO, customer, item);
        }
    }

    /**
     * Adds the item to the customer's existing cart. It checks if the item belongs to the partner's catalog
     * and adds the item to the cart if it is valid.
     *
     * @param cartItemDTO A CartItemDTO containing the details of the item to be added.
     * @param customer    The customer whose cart the item will be added to.
     * @param item        The item to be added to the cart.
     * @return A CartDTO representing the updated shopping cart after the item has been added.
     * @throws UnknownPartnerIdException     If no partner is found for the item in the cart.
     * @throws UnknownItemIdException        If the item is not valid for the partner's catalog.
     * @throws UnknownCustomerEmailException If the customer does not exist in the database.
     * @throws NoCartException               If the customer does not have a cart.
     */
    private CartDTO addItemToCart(CartItemAddItemToCartDTO cartItemDTO, Customer customer, Item item)
            throws UnknownPartnerIdException, UnknownItemIdException, UnknownCustomerEmailException, NoCartException {

        Cart customerCart = customer.getCart();
        if (customerCart == null) {
            throw new NoCartException(customer.getEmail());
        }

        // Check that the item belongs to the partner's catalog
        Partner partner = customerCart.getPartner();
        List<Item> partnerItems = partnerManager.findAllPartnerItems(partner.getPartnerId());
        if (partnerItems.stream().noneMatch(partnerItem -> partnerItem.getItemId().equals(item.getItemId()))) {
            throw new UnknownItemIdException(item.getItemId());
        }

        // Check if the item already exists in the cart
        boolean itemExists = false;
        for (CartItem existingCartItem : customerCart.getItemList()) {
            if (existingCartItem.getItem().getItemId().equals(item.getItemId())) {
                // If the item exists, update the quantity
                existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItemDTO.quantity());
                existingCartItem.setStartTime(cartItemDTO.startTime());
                existingCartItem.setEndTime(cartItemDTO.endTime());
                itemExists = true;
                break;
            }
        }

        // If the item doesn't exist, add a new item to the cart
        if (!itemExists) {
            CartItem newCartItem = new CartItem(item, cartItemDTO.quantity(), cartItemDTO.startTime(), cartItemDTO.endTime());
            customerCart.getItemList().add(newCartItem);
        }

        // Update the customer's cart
        Customer updatedCustomer = customerCatalog.setCart(customer.getEmail(), customerCart);
        return new CartDTO(updatedCustomer.getCart());
    }


    /**
     * Creates a new cart for the customer with the given item. The item is added to the new cart
     * which is then associated with the customer.
     *
     * @param cartItemDTO A CartItemDTO containing the details of the item to be added to the cart.
     * @param customer    The customer whose cart the item will be added to.
     * @param item        The item to be added to the new cart.
     * @return A CartDTO representing the newly created cart.
     * @throws UnknownPartnerIdException     If the partner for the item cannot be found.
     * @throws UnknownCustomerEmailException If the customer does not exist in the system.
     */
    private CartDTO createCart(CartItemAddItemToCartDTO cartItemDTO, Customer customer, Item item) throws UnknownPartnerIdException, UnknownCustomerEmailException {
        // Create the list of CartItem
        CartItem cartItem = new CartItem(item, cartItemDTO.quantity(), cartItemDTO.startTime(), cartItemDTO.endTime());
        Partner partner = partnerManager.findPartnerById(item.getPartner().getPartnerId());
        // Create the cart
        Cart cart = new Cart(partner, Set.of(cartItem), new ArrayList<>());
        customer = customerCatalog.setCart(customer.getEmail(), cart);
        return new CartDTO(customer.getCart());
    }


    /**
     * Retrieves the shopping cart of a customer by their email address. If the customer exists,
     * their cart is returned as a CartDTO. If the customer is not found, an exception is thrown.
     *
     * @param cartOwnerEmail The email address of the customer whose cart is to be retrieved.
     * @return An Optional containing a CartDTO representing the customer's cart,
     * or an empty Optional if the customer has no cart (though this is unlikely).
     * @throws UnknownCustomerEmailException If no customer is found with the given email.
     */
    @Override
    @Transactional
    public Optional<CartDTO> findCustomerCart(String cartOwnerEmail) throws UnknownCustomerEmailException {
        Customer customer = customerCatalog.findCustomerByEmail(cartOwnerEmail);
        Cart cart = customer.getCart();
        if (cart == null) {
            return Optional.empty();
        }
        return Optional.of(new CartDTO(cart));
    }

    /**
     * Removes a specified item from the customer's shopping cart. The method first validates that the customer exists,
     * then removes the item from the customer's cart if it exists. If the customer is not found, an exception is thrown.
     *
     * @param cartOwnerEmail The email address of the customer whose cart the item will be removed from.
     * @param cartItemDTO    A CartItemDTO representing the item to be removed, including the item ID.
     * @return A CartDTO representing the updated shopping cart after the item has been removed.
     * @throws UnknownCustomerEmailException If no customer is found with the given email.
     * @throws NoCartException               If the customer does not have a cart.
     * @throws EmptyCartException            If the cart is empty and no items can be removed.
     */
    @Override
    @Transactional
    public CartDTO removeItemFromCart(String cartOwnerEmail, CartItemDTO cartItemDTO) throws UnknownCustomerEmailException, EmptyCartException, NoCartException {
        // Check that the customer exists
        Customer customer = customerCatalog.findCustomerByEmail(cartOwnerEmail);
        Cart cart = verifyCart(customer);
        // Remove the item from the cart
        cart.getItemList().removeIf(cartItem -> cartItem.getItem().getItemId().equals(cartItemDTO.item().itemId()));
        Customer updatedCustomer = customerCatalog.setCart(cartOwnerEmail, cart);
        return new CartDTO(updatedCustomer.getCart());
    }

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
     * @throws EmptyCartException                  If the cart is empty and cannot be validated.
     * @throws NoCartException                     If the customer does not have a cart.
     */
    @Override
    @Transactional
    public PurchaseDTO validateCart(String cartOwnerEmail) throws UnknownCustomerEmailException, UnreachableExternalServiceException, EmptyCartException, NoCartException, ClosedTimeException {
        // Check that the customer exists
        Customer customer = customerCatalog.findCustomerByEmail(cartOwnerEmail);
        Cart cart = verifyCart(customer);
        // Create the purchase
        PaymentDTO paymentDTO = payment.makePay(customer);
        PurchaseDTO purchaseDTO = new PurchaseDTO(cartOwnerEmail, new CartDTO(cart), paymentDTO);
        customerCatalog.emptyCart(cartOwnerEmail);
        return purchaseDTO;
    }

    /**
     * Verifies the existence and validity of the customer's cart.
     * This helper method checks whether the customer has a cart and if the cart is not empty.
     *
     * @param customer The customer whose cart is being validated.
     * @return The cart associated with the customer if it exists and is not empty.
     * @throws NoCartException    If the customer does not have a cart.
     * @throws EmptyCartException If the customer's cart is empty.
     */
    private static Cart verifyCart(Customer customer) throws NoCartException, EmptyCartException {
        Cart cart = customer.getCart();
        if (cart == null) {
            throw new NoCartException(customer.getEmail());
        }
        if (cart.getItemList().isEmpty()) {
            throw new EmptyCartException(cart.getCartId());
        }
        return cart;
    }
}
