package fr.univcotedazur.teamj.kiwicard.entities.perks;

import fr.univcotedazur.teamj.kiwicard.dto.HappyKidsDiscountDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.CartItem;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.exceptions.BookingTimeNotSetException;
import fr.univcotedazur.teamj.kiwicard.exceptions.ClosedTimeException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
import fr.univcotedazur.teamj.kiwicard.interfaces.IHappyKids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class PerkApplicationVisitorImpl implements PerkApplicationVisitor {

    private final IHappyKids happyKidsProxy;

    @Autowired
    public PerkApplicationVisitorImpl(IHappyKids happyKidsProxy) {
        this.happyKidsProxy = happyKidsProxy;
    }

    /**
     * Applies a discount to the items in the customer's cart based on the perk's specified interval.
     *
     * <p>This method checks each item in the customer's cart for whether the item falls within the
     * perk interval. If any part of the booking period (defined by the item's start time and quantity)
     * intersects the perk interval, the corresponding discount is computed and applied to the item.
     *
     * <p>If no items in the cart fall within the perk interval, the method returns {@code false}.
     * Otherwise, it returns {@code true}.
     *
     * @param perk     the perk that defines the discount rate and the interval during which the discount applies
     * @param customer the customer whose cart items are being evaluated for discounts
     * @return {@code true} if at least one item in the cart was eligible for a discount, {@code false} otherwise
     * @throws ClosedTimeException                 if the cart contains items with invalid booking times
     * @throws UnreachableExternalServiceException if an external service, such as a discount provider, is unavailable
     * @throws BookingTimeNotSetException          if any of the cart items do not have a valid start time
     */
    @Override
    public boolean visit(VfpDiscountInPercentPerk perk, Customer customer)
            throws ClosedTimeException, UnreachableExternalServiceException, BookingTimeNotSetException {
        List<CartItem> hkItems = customer.getCart().getHKItems();
        if (hkItems.isEmpty()) {
            return false;
        }
        for (CartItem item : hkItems) {
            LocalDateTime startTime = item.getStartTime();
            if (startTime == null) {
                throw new BookingTimeNotSetException();
            }
            // Get the number of hours that fall within the perk interval
            int hoursInPerkInterval = getHoursInPerkInterval(item, perk.getStartHour(), perk.getEndHour());

            // If any hours fall within the perk interval, apply the discount
            if (hoursInPerkInterval > 0) {
                HappyKidsDiscountDTO discountDTO = happyKidsProxy.computeDiscount(item.getItemPrice() * hoursInPerkInterval, perk.getDiscountRate());
                if (discountDTO != null) {
                    item.setPrice(discountDTO.price());
                }
            }
        }
        return true;
    }

    /**
     * Determines how many hours in the given booking period fall within the perk interval.
     *
     * <p>If the perk interval does not cross midnight (i.e. start is before or equal to end),
     * the bookingTime must be equal to or after the start and strictly before the end.
     * If the interval crosses midnight, the bookingTime is valid if it is on or after the start
     * or before the end.
     *
     * @param cartItem  the item to check
     * @param perkStart the start time of the perk interval
     * @param perkEnd   the end time of the perk interval
     * @return the number of hours within the perk interval
     */
    int getHoursInPerkInterval(CartItem cartItem, LocalTime perkStart, LocalTime perkEnd) {
        int hoursInPerkInterval = 0;
        LocalTime bookingTime = cartItem.getStartTime().toLocalTime();
        // Iterate through all hours from the start time and check if they fall within the perk interval
        for (int i = 0; i < cartItem.getQuantity(); i++) {
            LocalTime currentTime = bookingTime.plusHours(i);
            if (isWithinPerkInterval(currentTime, perkStart, perkEnd)) {
                hoursInPerkInterval++;
            }
        }

        return hoursInPerkInterval;
    }

    /**
     * Determines if the given bookingTime falls within the perk interval.
     *
     * <p>If the perk interval does not cross midnight (i.e. start is before or equal to end),
     * the bookingTime must be equal to or after the start and strictly before the end.
     * If the interval crosses midnight, the bookingTime is valid if it is on or after the start
     * or before the end.
     *
     * @param bookingTime the time to check
     * @param perkStart   the start time of the perk interval
     * @param perkEnd     the end time of the perk interval
     * @return true if bookingTime is within the interval, false otherwise
     */
    private boolean isWithinPerkInterval(LocalTime bookingTime, LocalTime perkStart, LocalTime perkEnd) {
        if (!perkStart.isAfter(perkEnd)) { // same-day interval
            return !bookingTime.isBefore(perkStart) && bookingTime.isBefore(perkEnd);
        }
        // interval crosses midnight (e.g. 21:00 to 02:00)
        return !bookingTime.isBefore(perkStart) || bookingTime.isBefore(perkEnd);
    }


    /**
     * Application d'une réduction à partir d'une certaine heure
     *
     * @param perk le perk à appliquer
     * @return true si le perk a été appliqué, false sinon
     */
    @Override
    public boolean visit(TimedDiscountInPercentPerk perk, Customer customer) {
        // Application d'une réduction temporelle
        if (!LocalTime.now().isAfter(perk.getTime())) {
            return false;
        }
        customer.getCart().addToTotalPercentageReduction(perk.getDiscountRate());
        return true;
    }

    /**
     * Application pour le perk "Buy N get M free"
     *
     * @param perk le perk à appliquer
     * @return true si le perk a été appliqué, false sinon
     */
    @Override
    public boolean visit(NPurchasedMGiftedPerk perk, Customer customer) {
        // Application pour le perk "Buy N get M free"
        Cart cart = customer.getCart();
        CartItem cartItem = cart.getItemById(perk.getItem().getItemId());
        if (cartItem != null && cartItem.getQuantity() >= perk.getNbPurchased()) {
            cartItem.addFreeItem(perk.getNbGifted());
            return true;
        }
        return false;
    }
}
