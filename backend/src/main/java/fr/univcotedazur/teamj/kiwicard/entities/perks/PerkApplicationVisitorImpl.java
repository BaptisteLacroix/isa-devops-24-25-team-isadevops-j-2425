package fr.univcotedazur.teamj.kiwicard.entities.perks;

import fr.univcotedazur.teamj.kiwicard.dto.HappyKidsDiscountDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.CartItem;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
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
     * Application d'une réduction pour la garde d'enfants HappyKids
     *
     * @param perk le perk à appliquer
     * @return true si le perk a été appliqué, false sinon
     * @throws ClosedTimeException                 si le service externe est fermé
     * @throws UnreachableExternalServiceException si le service externe est injoignable
     */
    @Override
    public boolean visit(VfpDiscountInPercentPerk perk, Customer customer)
            throws ClosedTimeException, UnreachableExternalServiceException {
        List<CartItem> hkItems = customer.getCart().getHKItems();
        if (hkItems.isEmpty()) {
            return false;
        }
        for (CartItem item : hkItems) {
            LocalDateTime startTime = item.getStartTime();
            if (startTime == null) {
                // TODO : Custom exception
                throw new IllegalStateException("Booking time is not set");
            }
            LocalTime bookingTime = startTime.toLocalTime();
            boolean isWithinPerkInterval = isWithinPerkInterval(bookingTime, perk.getStartHour(), perk.getEndHour());
            if (isWithinPerkInterval) {
                HappyKidsDiscountDTO discountDTO = happyKidsProxy.computeDiscount(item, perk.getDiscountRate());
                if (discountDTO != null) {
                    item.setPrice(discountDTO.price());
                }
            }
        }
        return true;
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
     * @param perkStart the start time of the perk interval
     * @param perkEnd the end time of the perk interval
     * @return true if bookingTime is within the interval, false otherwise
     */
    private boolean isWithinPerkInterval(LocalTime bookingTime, LocalTime perkStart, LocalTime perkEnd) {
        if (!perkStart.isAfter(perkEnd)) { // same-day interval
            return !bookingTime.isBefore(perkStart) && bookingTime.isBefore(perkEnd);
        }
        // interval crosses midnight (e.g. 21:00 to 02:00)
        return !bookingTime.isBefore(perkStart) && bookingTime.isAfter(perkEnd);
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
     * @param perk le perk à appliquer
     * @return true si le perk a été appliqué, false sinon
     */
    @Override
    public boolean visit(NPurchasedMGiftedPerk perk, Customer customer) {
        // Application pour le perk "Buy N get M free"
        Cart cart = customer.getCart();
        CartItem cartItem = cart.getItemById(perk.getItem().getItemId());
        if (cartItem != null && cartItem.getQuantity() >= perk.getNbPurchased()) {
            cartItem.increaseQuantity(perk.getNbGifted());
            return true;
        }
        return false;
    }
}
