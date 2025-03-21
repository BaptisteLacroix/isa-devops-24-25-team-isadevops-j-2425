package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.connectors.BankProxy;
import fr.univcotedazur.teamj.kiwicard.connectors.HappyKidsProxy;
import fr.univcotedazur.teamj.kiwicard.connectors.externaldto.PaymentRequestDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PaymentDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PaymentResponseDTO;
import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.entities.perks.AbstractPerk;
import fr.univcotedazur.teamj.kiwicard.entities.perks.PerkApplicationVisitor;
import fr.univcotedazur.teamj.kiwicard.entities.perks.PerkApplicationVisitorImpl;
import fr.univcotedazur.teamj.kiwicard.exceptions.ClosedTimeException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
import fr.univcotedazur.teamj.kiwicard.interfaces.IPayment;
import fr.univcotedazur.teamj.kiwicard.mappers.PerkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Cashier} class is responsible for handling the payment process of a customer.
 * It interacts with external banking services via {@link BankProxy} to process payments.
 * The class implements the {@link IPayment} interface to define the payment behavior.
 */
@Component
public class Cashier implements IPayment {

    private final BankProxy bankProxy;
    private final HappyKidsProxy happyKidsProxy;

    /**
     * Constructs a {@code Cashier} instance with the specified {@link BankProxy} dependency.
     *
     * @param bankProxy The proxy that handles interactions with the external banking system.
     */
    @Autowired
    public Cashier(BankProxy bankProxy, HappyKidsProxy happyKidsProxy) {
        this.bankProxy = bankProxy;
        this.happyKidsProxy = happyKidsProxy;
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
     */
    @Override
    public PaymentDTO makePay(Customer customer) throws UnreachableExternalServiceException, ClosedTimeException {
        PaymentResponseDTO paymentResponseDTO = computePrice(customer);
        // Prepare the payment request and process it via the bank proxy
        PaymentRequestDTO paymentRequestDTO = new PaymentRequestDTO(customer.getCardNumber(), paymentResponseDTO.totalPrice());
        return bankProxy.askPayment(paymentRequestDTO);
    }

    PaymentResponseDTO computePrice(Customer customer) throws ClosedTimeException, UnreachableExternalServiceException {
        Cart cart = customer.getCart();
        // Calculate the total price before applying discounts
        double percentage = cart.getTotalPercentageReduction();
        double totalPriceWithoutReduction = cart.getTotalPrice();
        double totalPrice = totalPriceWithoutReduction - (totalPriceWithoutReduction * percentage);
        // Reset the total percentage reduction
        cart.resetTotalPercentageReduction();
        List<IPerkDTO> successfulPerks = new ArrayList<>();
        // Apply perks to the customer
        PerkApplicationVisitor visitor = new PerkApplicationVisitorImpl(customer, happyKidsProxy);
        List<AbstractPerk> perksToRemove = new ArrayList<>();
        for (AbstractPerk perk : cart.getPerksToUse()) {
            if (perk.apply(visitor)) {
                successfulPerks.add(PerkMapper.toDTO(perk));
                perksToRemove.add(perk);  // Collect perks to be removed
            }
        }

        // After the loop, update the cart with all modifications at once
        for (AbstractPerk perk : perksToRemove) {
            cart.updatePerksUsed(perk);
        }

        // Calculate the total price after applying discounts
        percentage = cart.getTotalPercentageReduction();
        // Recalculate the total price after applying discounts
        if (percentage != 0) totalPrice = totalPriceWithoutReduction - (totalPriceWithoutReduction * percentage);
        return new PaymentResponseDTO(totalPrice, successfulPerks);
    }

}
