package fr.univcotedazur.teamj.kiwicard.entities.perks;

import fr.univcotedazur.teamj.kiwicard.connectors.HappyKidsProxy;
import fr.univcotedazur.teamj.kiwicard.dto.HappyKidsDiscountDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.CartItem;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.exceptions.ClosedTimeException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class PerkApplicationVisitorImpl implements PerkApplicationVisitor {

    private final Customer customer;
    private final HappyKidsProxy happyKidsProxy;

    public PerkApplicationVisitorImpl(Customer customer, HappyKidsProxy happyKidsProxy) {
        this.customer = customer;
        this.happyKidsProxy = happyKidsProxy;
    }

    @Override
    public boolean visit(VfpDiscountInPercentPerk perk) throws ClosedTimeException, UnreachableExternalServiceException {
        List<CartItem> hkItems = customer.getCart().getHKItems(null);
        for (CartItem item : hkItems) {
            LocalDateTime startTime = item.getStartTime();
            if (startTime == null) {
                throw new IllegalStateException("Booking time is not set");
            }
            LocalTime bookingTime = startTime.toLocalTime();
            if (!bookingTime.isBefore(perk.getStartHour()) && bookingTime.isBefore(perk.getEndHour())) {
                HappyKidsDiscountDTO discountDTO = happyKidsProxy.computeDiscount(item, perk.getDiscountRate());
                if (discountDTO != null) {
                    item.setPrice(discountDTO.price());
                }
            }
        }
        return true;
    }

    @Override
    public boolean visit(TimedDiscountInPercentPerk perk) {
        // Application d'une réduction temporelle
        if (!LocalTime.now().isAfter(perk.getTime())) {
            return false;
        }
        customer.getCart().addToTotalPercentageReduction(perk.getDiscountRate());
        return true;
    }

    @Override
    public boolean visit(NPurchasedMGiftedPerk perk) {
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
