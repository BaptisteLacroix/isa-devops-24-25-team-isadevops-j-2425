package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CartInPurchaseDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CartItemAddDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PaymentDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PurchaseDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.CartItem;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.entities.Partner;
import fr.univcotedazur.teamj.kiwicard.exceptions.AlreadyBookedTimeException;
import fr.univcotedazur.teamj.kiwicard.exceptions.BookingTimeNotSetException;
import fr.univcotedazur.teamj.kiwicard.exceptions.ClosedTimeException;
import fr.univcotedazur.teamj.kiwicard.exceptions.EmptyCartException;
import fr.univcotedazur.teamj.kiwicard.exceptions.NoCartException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownItemIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
import fr.univcotedazur.teamj.kiwicard.interfaces.IPayment;
import fr.univcotedazur.teamj.kiwicard.interfaces.cart.ICartFinder;
import fr.univcotedazur.teamj.kiwicard.interfaces.cart.ICartModifier;
import fr.univcotedazur.teamj.kiwicard.interfaces.partner.IPartnerManager;
import fr.univcotedazur.teamj.kiwicard.repositories.IItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public CartDTO addItemToCart(String customerEmail, CartItemAddDTO cartItemDTO, CartDTO cartDTO) throws UnknownCustomerEmailException, UnknownPartnerIdException, UnknownItemIdException, NoCartException, AlreadyBookedTimeException {
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
     * @param cartItemDTOToAdd A CartItemDTO containing the details of the item to be added.
     * @param customer         The customer whose cart the item will be added to.
     * @param itemToAdd        The item to be added to the cart.
     * @return A CartDTO representing the updated shopping cart after the item has been added.
     * @throws UnknownPartnerIdException     If no partner is found for the item in the cart.
     * @throws UnknownItemIdException        If the item is not valid for the partner's catalog.
     * @throws UnknownCustomerEmailException If the customer does not exist in the database.
     * @throws NoCartException               If the customer does not have a cart.
     */
    private CartDTO addItemToCart(CartItemAddDTO cartItemDTOToAdd, Customer customer, Item itemToAdd)
            throws UnknownPartnerIdException, UnknownItemIdException, UnknownCustomerEmailException, NoCartException, AlreadyBookedTimeException {

        Cart customerCart = verifyCartExists(customer);

        verifyItemIsSoldBySamePartnerThanCart(itemToAdd, customerCart.getPartner());

        if (customerCart.alreadyContains(itemToAdd)) {
            updateItemQuantity(cartItemDTOToAdd, itemToAdd, customerCart);
        } else {
            doAddItemToCart(cartItemDTOToAdd, itemToAdd, customerCart);
        }

        // Update the customer's cart
        Customer updatedCustomer = customerCatalog.setCart(customer.getEmail(), customerCart);
        return new CartDTO(updatedCustomer.getCart());

    }

    /**
     * Gestion de la mise à jour de la quantité d'un item dans le panier
     *
     * @param cartItemDTOToAdd le DTO de l'item à ajouter
     * @param itemToAdd        l'item à ajouter
     * @param customerCart     le panier du client
     * @throws AlreadyBookedTimeException si le créneau est déjà réservé dans le cas d'une réservation
     */
    private static void updateItemQuantity(CartItemAddDTO cartItemDTOToAdd, Item itemToAdd, Cart customerCart) throws AlreadyBookedTimeException {
        CartItem existingCartItem = customerCart.getItemById(itemToAdd.getItemId());
        if (!cartItemDTOToAdd.isABooking()) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItemDTOToAdd.quantity());
            existingCartItem.setStartTime(null);
        } else if (timeIsAlreadyBooked(cartItemDTOToAdd, existingCartItem)) {
            LocalDateTime alreadyBookedTime = existingCartItem.getStartTime();
            throw new AlreadyBookedTimeException(itemToAdd.getLabel(), alreadyBookedTime, existingCartItem.getQuantity());
        } else {
            doAddItemToCart(cartItemDTOToAdd, itemToAdd, customerCart);
        }
    }

    private static void doAddItemToCart(CartItemAddDTO cartItemDTOToAdd, Item itemToAdd, Cart customerCart) {
        CartItem newCartItem = new CartItem(itemToAdd, cartItemDTOToAdd);
        customerCart.getItems().add(newCartItem);
    }

    /**
     * Vérifie si le panier du client existe
     *
     * @param customer le client
     * @return le panier du client
     * @throws NoCartException si le panier n'existe pas
     */
    private static Cart verifyCartExists(Customer customer) throws NoCartException {
        Cart customerCart = customer.getCart();
        if (customerCart == null) {
            throw new NoCartException(customer.getEmail());
        }
        return customerCart;
    }

    /**
     * Check if the time to book is already booked
     *
     * @param itemToBookDTO    the item to book
     * @param existingCartItem the existing item in the cart
     * @return true if the time is already booked, false otherwise
     */
    private static boolean timeIsAlreadyBooked(CartItemAddDTO itemToBookDTO, CartItem existingCartItem) {
        LocalDateTime startTimeToBook = itemToBookDTO.startTime();
        LocalDateTime endTimeToBook = itemToBookDTO.startTime().plusHours(itemToBookDTO.quantity());
        LocalDateTime startTimeBooked = existingCartItem.getStartTime();
        LocalDateTime endTimeBooked = existingCartItem.getStartTime().plusHours(existingCartItem.getQuantity());
        return startTimeToBook.isEqual(startTimeBooked) || endTimeToBook.isEqual(endTimeBooked) ||
                (startTimeToBook.isAfter(startTimeBooked) && startTimeToBook.isBefore(endTimeBooked)) ||
                (endTimeToBook.isAfter(startTimeBooked) && endTimeToBook.isBefore(endTimeBooked));


    }

    /**
     * Vérifie si l'item à ajouter est vendu par le même partenaire que celui des items déjà dans le panier
     *
     * @param itemToAdd  l'item à ajouter
     * @param cartSeller le partenaire du panier
     * @throws UnknownPartnerIdException si le partenaire n'existe pas
     * @throws UnknownItemIdException    si l'item n'existe pas
     */
    private void verifyItemIsSoldBySamePartnerThanCart(Item itemToAdd, Partner cartSeller) throws UnknownPartnerIdException, UnknownItemIdException {
        List<Item> partnerItems = partnerManager.findAllPartnerItems(cartSeller.getPartnerId());
        if (partnerItems.stream().noneMatch(partnerItem -> partnerItem.getItemId().equals(itemToAdd.getItemId()))) {
            throw new UnknownItemIdException(itemToAdd.getItemId());
        }
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
    private CartDTO createCart(CartItemAddDTO cartItemDTO, Customer customer, Item item) throws UnknownPartnerIdException, UnknownCustomerEmailException {
        // Create the list of CartItem
        CartItem cartItem = new CartItem(item, cartItemDTO);
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
     * @param itemId         The item ID of the item to be removed from the cart.
     * @return A CartDTO representing the updated shopping cart after the item has been removed.
     * @throws UnknownCustomerEmailException If no customer is found with the given email.
     * @throws NoCartException               If the customer does not have a cart.
     * @throws EmptyCartException            If the cart is empty and no items can be removed.
     */
    @Override
    @Transactional
    public CartDTO removeItemFromCart(String cartOwnerEmail, Long itemId) throws UnknownCustomerEmailException, EmptyCartException, NoCartException, UnknownItemIdException {
        // Check that the customer exists
        Customer customer = customerCatalog.findCustomerByEmail(cartOwnerEmail);
        Cart cart = verifyCart(customer);
        // Remove the item from the cart
        boolean removed = cart.getItems().removeIf(cartItem -> cartItem.getItem().getItemId().equals(itemId));
        if (!removed) {
            throw new UnknownItemIdException(itemId);
        }
        if (cart.getItems().isEmpty()) {
            customerCatalog.resetCart(cartOwnerEmail);
            return null;
        }else{
            Customer updatedCustomer = customerCatalog.setCart(cartOwnerEmail, cart);
            return new CartDTO(updatedCustomer.getCart());
        }
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
    public PurchaseDTO validateCart(String cartOwnerEmail) throws UnknownCustomerEmailException, UnreachableExternalServiceException, EmptyCartException, NoCartException, ClosedTimeException, BookingTimeNotSetException {
        // Check that the customer exists
        Customer customer = customerCatalog.findCustomerByEmail(cartOwnerEmail);
        Cart cart = verifyCart(customer);
        // Create the purchase
        PaymentDTO paymentDTO = payment.makePay(customer);
        PurchaseDTO purchaseDTO = new PurchaseDTO(cartOwnerEmail, new CartInPurchaseDTO(cart), paymentDTO);
        customerCatalog.resetCart(cartOwnerEmail);
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
        Cart cart = verifyCartExists(customer);
        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException(cart.getCartId());
        }
        return cart;
    }
}
