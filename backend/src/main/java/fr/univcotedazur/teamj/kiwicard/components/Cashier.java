package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.connectors.BankProxy;
import fr.univcotedazur.teamj.kiwicard.connectors.externaldto.PaymentRequestDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PaymentDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PaymentResponseDTO;
import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.entities.perks.AbstractPerk;
import fr.univcotedazur.teamj.kiwicard.entities.perks.PerkApplicationVisitor;
import fr.univcotedazur.teamj.kiwicard.entities.perks.PerkApplicationVisitorImpl;
import fr.univcotedazur.teamj.kiwicard.exceptions.BookingTimeNotSetException;
import fr.univcotedazur.teamj.kiwicard.exceptions.ClosedTimeException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
import fr.univcotedazur.teamj.kiwicard.interfaces.IHappyKids;
import fr.univcotedazur.teamj.kiwicard.interfaces.IPayment;
import fr.univcotedazur.teamj.kiwicard.interfaces.purchase.IPurchaseCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;

import static fr.univcotedazur.teamj.kiwicard.configurations.Constants.MAX_DISCOUNT_RATE_OF_A_CART;

/**
 * The {@code Cashier} class is responsible for handling the payment process of a customer.
 * It interacts with external banking services via {@link BankProxy} to process payments.
 * The class implements the {@link IPayment} interface to define the payment behavior.
 */
@Component
public class Cashier implements IPayment {

    private final BankProxy bankProxy;
    private final IHappyKids happyKidsProxy;
    private final IPurchaseCreator purchaseCreator;

    /**
     * Constructs a {@code Cashier} instance with the specified {@link BankProxy} and {@link IHappyKids} dependencies.
     *
     * @param bankProxy The proxy that handles interactions with the external banking system.
     * @param happyKidsProxy The proxy that handles interactions with the HappyKids service.
     */
    @Autowired
    public Cashier(BankProxy bankProxy, IHappyKids happyKidsProxy, IPurchaseCreator purchaseCreator) {
        this.bankProxy = bankProxy;
        this.happyKidsProxy = happyKidsProxy;
        this.purchaseCreator = purchaseCreator;
    }

    /**
     * Processes the payment for the given {@link Customer}.
     * It first calculates the total price before applying discounts, applies any applicable perks to the customer,
     * and then recalculates the total price after the perks are applied. The method sends the payment request to the
     * external bank system through the {@link BankProxy} for processing.
     *
     * @param customer The customer whose payment is being processed. The customer's cart is used to calculate
     *                 the total price and apply any perks.
     * @return A {@link PaymentDTO} containing the result of the payment transaction, including the final totalPrice to be paid.
     * @throws UnreachableExternalServiceException If the external bank service is unreachable or fails during the payment request.
     * @throws ClosedTimeException If the payment is attempted outside of allowed business hours.
     * @throws BookingTimeNotSetException If the booking time is not set for the customer.
     */
    @Override
    public PaymentDTO makePay(Customer customer) throws UnreachableExternalServiceException, ClosedTimeException, BookingTimeNotSetException {
        PaymentResponseDTO paymentResponseDTO = computePurchaseTotalPrice(customer);
        // Prepare the payment request and process it via the bank proxy
        PaymentRequestDTO paymentRequestDTO = new PaymentRequestDTO(customer.getCardNumber(), paymentResponseDTO.totalPrice());
        PaymentDTO paymentDTO = bankProxy.askPayment(paymentRequestDTO);
        purchaseCreator.createPurchase(customer, paymentResponseDTO.totalPrice());
        return paymentDTO;
    }

    /**
     * Computes the total price for the given {@link Customer} after applying any applicable perks.
     *
     * @param customer The customer whose cart is being processed.
     * @return A {@link PaymentResponseDTO} containing the total price and the list of successfully applied perks.
     * @throws ClosedTimeException If the computation is attempted outside of allowed business hours.
     * @throws UnreachableExternalServiceException If an external service is unreachable during the computation.
     * @throws BookingTimeNotSetException If the booking time is not set for the customer.
     */
    PaymentResponseDTO computePurchaseTotalPrice(Customer customer) throws ClosedTimeException, UnreachableExternalServiceException, BookingTimeNotSetException {
        Cart cart = customer.getCart();
        // Apply perks to the customer
        applyPerksToCart(cart, customer);
        // Calculate the total price after applying discounts
        double percentage = Math.min(cart.getTotalPercentageReduction(),MAX_DISCOUNT_RATE_OF_A_CART);
        double totalPriceWithoutReduction = cart.getTotalPrice();
        // Recalculate the total price after applying discounts
        double totalPrice = totalPriceWithoutReduction - (totalPriceWithoutReduction * (percentage/100));
        return new PaymentResponseDTO(totalPrice);
    }

    /**
     * Applies the perks to the given {@link Cart} and returns the list of successfully applied perks.
     *
     * @param cart The cart to which the perks are applied.
     * @param customer The customer whose cart is being processed.
     */
    private void applyPerksToCart(Cart cart, Customer customer) throws BookingTimeNotSetException, ClosedTimeException, UnreachableExternalServiceException {
        PerkApplicationVisitor visitor = new PerkApplicationVisitorImpl(happyKidsProxy);
        Iterator<AbstractPerk> iterator = cart.getPerksToUse().iterator();

        while (iterator.hasNext()) {
            AbstractPerk perk = iterator.next();
            if (perk.apply(visitor, customer)) {
                iterator.remove();  // Safely remove the perk from the list
                cart.addPerkUsed(perk);
            }
        }
    }
}
