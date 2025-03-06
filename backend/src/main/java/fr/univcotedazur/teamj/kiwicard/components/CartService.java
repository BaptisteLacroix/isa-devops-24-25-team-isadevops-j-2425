package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.dto.CartCreationDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CartItemDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CustomerDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PartnerCreationDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PartnerDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PaymentDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PurchaseDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.CartItem;
import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.entities.Partner;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownItemIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
import fr.univcotedazur.teamj.kiwicard.interfaces.IPayment;
import fr.univcotedazur.teamj.kiwicard.interfaces.cart.ICartCreator;
import fr.univcotedazur.teamj.kiwicard.interfaces.cart.ICartFinder;
import fr.univcotedazur.teamj.kiwicard.interfaces.cart.ICartModifier;
import fr.univcotedazur.teamj.kiwicard.interfaces.partner.IPartnerManager;
import fr.univcotedazur.teamj.kiwicard.repositories.IItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class CartService implements ICartModifier, ICartFinder, ICartCreator {

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
    @Override
    @Transactional
    public CartCreationDTO createCart(String customerEmail, Long partnerId, List<CartItemDTO> cartItemDTOS) throws UnknownCustomerEmailException, UnknownPartnerIdException, UnknownItemIdException {
        // Found the customer in the bdd
        customerCatalog.findCustomerByEmail(customerEmail).orElseThrow(UnknownCustomerEmailException::new);

        // Check that the partner exists
        List<Item> partnerItems = partnerManager.findAllPartnerItems(partnerId);

        // Found the item in the repository and check that it exists and that it's part of the partner catalog
        for (CartItemDTO cartItemDTO : cartItemDTOS) {
            Item item = itemRepository.findById(cartItemDTO.itemId()).orElseThrow(() -> new UnknownItemIdException(cartItemDTO.itemId()));
            if (partnerItems.stream().noneMatch(partnerItem -> partnerItem.getItemId().equals(item.getItemId()))) {
                throw new UnknownItemIdException(item.getItemId());
            }
        }

        // Create the list of CartItem
        List<CartItem> cartItems = new ArrayList<>();

        for (CartItemDTO cartItemDTO : cartItemDTOS) {
            Item item = itemRepository.findById(cartItemDTO.itemId())
                    .orElseThrow(() -> new UnknownItemIdException(cartItemDTO.itemId()));

            CartItem cartItem = new CartItem(item, cartItemDTO.quantity(), cartItemDTO.startTime(), cartItemDTO.endTime());
            cartItems.add(cartItem);
        }

        PartnerDTO partnerDTO = partnerManager.findPartnerById(partnerId);
        Partner partner = new Partner(new PartnerCreationDTO(partnerDTO.name(), partnerDTO.address()));

        // Create the cart
        CartCreationDTO cartCreationDTO = new CartCreationDTO(partner, new HashSet<>(cartItems), new ArrayList<>());
        customerCatalog.setCart(customerEmail, cartCreationDTO);

        return cartCreationDTO;
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
    public Optional<CartDTO> findCustomerCart(String cartOwnerEmail) throws UnknownCustomerEmailException {
        CustomerDTO customer = customerCatalog.findCustomerByEmail(cartOwnerEmail).orElseThrow(UnknownCustomerEmailException::new);
        return Optional.of(customer.cartDTO());
    }

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
    @Override
    @Transactional
    public CartDTO addItemToCart(String cartOwnerEmail, CartItemDTO cartItemDTO) throws UnknownCustomerEmailException, UnknownItemIdException, UnknownPartnerIdException {
        // Check that the customer exists
        CustomerDTO customer = customerCatalog.findCustomerByEmail(cartOwnerEmail).orElseThrow(UnknownCustomerEmailException::new);

        // Check that the cart item is valid
        Item item = itemRepository.findById(cartItemDTO.itemId()).orElseThrow(() -> new UnknownItemIdException(cartItemDTO.itemId()));

        // Check that the item belongs to the partner's catalog
        PartnerDTO partner = customer.cartDTO().partner();
        List<Item> partnerItems = partnerManager.findAllPartnerItems(partner.id());
        if (partnerItems.stream().noneMatch(partnerItem -> partnerItem.getItemId().equals(item.getItemId()))) {
            throw new UnknownItemIdException(item.getItemId());
        }

        // Add the item to the cart
        CartItem cartItem = new CartItem(item, cartItemDTO.quantity(), cartItemDTO.startTime(), cartItemDTO.endTime());
        customer.cartDTO().items().add(new CartItemDTO(cartItem));

        customerCatalog.setCart(cartOwnerEmail, customer.cartDTO());

        return customer.cartDTO();
    }

    /**
     * Removes a specified item from the customer's shopping cart. The method first validates that the customer exists,
     * then removes the item from the customer's cart if it exists. If the customer is not found, an exception is thrown.
     *
     * @param cartOwnerEmail The email address of the customer whose cart the item will be removed from.
     * @param cartItemDTO    A CartItemDTO representing the item to be removed, including the item ID.
     * @return A CartDTO representing the updated shopping cart after the item has been removed.
     * @throws UnknownCustomerEmailException If no customer is found with the given email.
     */
    @Override
    @Transactional
    public CartDTO removeItemFromCart(String cartOwnerEmail, CartItemDTO cartItemDTO) throws UnknownCustomerEmailException {
        // Check that the customer exists
        CustomerDTO customer = customerCatalog.findCustomerByEmail(cartOwnerEmail).orElseThrow(UnknownCustomerEmailException::new);

        // Remove the item from the cart
        customer.cartDTO().items().removeIf(cartItem -> cartItem.itemId().equals(cartItemDTO.itemId()));

        customerCatalog.setCart(cartOwnerEmail, customer.cartDTO());

        return customer.cartDTO();
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
     */
    @Override
    @Transactional
    public PurchaseDTO validateCart(String cartOwnerEmail) throws UnknownCustomerEmailException, UnreachableExternalServiceException {
        // Check that the customer exists
        CustomerDTO customer = customerCatalog.findCustomerByEmail(cartOwnerEmail).orElseThrow(UnknownCustomerEmailException::new);

        // Create the purchase
        PaymentDTO paymentDTO = payment.makePay(customer);

        return new PurchaseDTO(cartOwnerEmail, customer.cartDTO(), paymentDTO);
    }
}
