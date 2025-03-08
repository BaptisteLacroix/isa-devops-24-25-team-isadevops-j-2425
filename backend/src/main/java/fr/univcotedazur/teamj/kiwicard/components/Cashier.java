package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.connectors.BankProxy;
import fr.univcotedazur.teamj.kiwicard.connectors.externaldto.PaymentRequestDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PaymentDTO;
import fr.univcotedazur.teamj.kiwicard.entities.CartItem;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.entities.perks.AbstractPerk;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
import fr.univcotedazur.teamj.kiwicard.interfaces.IPayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The {@code Cashier} class is responsible for handling the payment process of a customer.
 * It interacts with external banking services via {@link BankProxy} to process payments.
 * The class implements the {@link IPayment} interface to define the payment behavior.
 */
@Component
public class Cashier implements IPayment {

    private final BankProxy bankProxy;

    /**
     * Constructs a {@code Cashier} instance with the specified {@link BankProxy} dependency.
     *
     * @param bankProxy The proxy that handles interactions with the external banking system.
     */
    @Autowired
    public Cashier(BankProxy bankProxy) {
        this.bankProxy = bankProxy;
    }

    /**
     * Processes the payment for the given {@link Customer}.
     * It first calculates the total price before applying discounts, applies any applicable perks to the customer,
     * and then recalculates the total price after the perks are applied. The method sends the payment request to the
     * external bank system through the {@link BankProxy} for processing.
     *
     * @param customer The customer whose payment is being processed. The customer's cart is used to calculate
     *                 the total price and apply any perks.
     * @return A {@link PaymentDTO} containing the result of the payment transaction, including the final amount to be paid.
     * @throws UnreachableExternalServiceException If the external bank service is unreachable or fails during the payment request.
     */
    @Override
    public PaymentDTO makePay(Customer customer) throws UnreachableExternalServiceException {
        // Calculate the total price before applying discounts
        double percentage = customer.getCart().getTotalPercentageReduction();
        double totalPriceWithoutReduction = customer.getCart().getItemList().stream().mapToDouble(CartItem::getPrice).sum();
        double totalPrice = totalPriceWithoutReduction - (totalPriceWithoutReduction * percentage);

        // Reset the total percentage reduction
        customer.getCart().addToTotalPercentageReduction(percentage);

        // Apply perks to the customer
        for (AbstractPerk perk : customer.getCart().getPerksToUse()) {
            perk.apply(customer);
        }

        // Calculate the total price after applying discounts
        percentage = customer.getCart().getTotalPercentageReduction();
        // Recalculate the total price after applying discounts
        if (percentage != 0) totalPrice = totalPriceWithoutReduction - (totalPriceWithoutReduction * percentage);

        // Prepare the payment request and process it via the bank proxy
        PaymentRequestDTO paymentRequestDTO = new PaymentRequestDTO(customer.getCardNumber(), totalPrice);
        return bankProxy.askPayment(paymentRequestDTO);
    }

}
