package fr.univcotedazur.teamj.kiwicard.entities.perks;

import fr.univcotedazur.teamj.kiwicard.dto.perks.VfpDiscountInPercentPerkDTO;
import fr.univcotedazur.teamj.kiwicard.entities.CartItem;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.exceptions.BookingTimeNotSetException;
import fr.univcotedazur.teamj.kiwicard.exceptions.ClosedTimeException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
import fr.univcotedazur.teamj.kiwicard.mappers.PerkVisitor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import static fr.univcotedazur.teamj.kiwicard.configurations.Constants.MAX_DISCOUNT_RATE_OF_A_PERK;
import static fr.univcotedazur.teamj.kiwicard.configurations.Constants.MIN_DISCOUNT_RATE_OF_A_PERK;

@Entity
public class VfpDiscountInPercentPerk extends AbstractPerk {
    @Column
    @NotNull
    private double discountRate;

    @Column
    @NotNull
    private LocalTime startHour;

    @Column
    @NotNull
    private LocalTime endHour;


    public VfpDiscountInPercentPerk() {
    }

    public VfpDiscountInPercentPerk(double discountRate, LocalTime startHour, LocalTime endHour) {
        if (discountRate > MAX_DISCOUNT_RATE_OF_A_PERK || discountRate < MIN_DISCOUNT_RATE_OF_A_PERK) {
            throw new IllegalArgumentException("Discount rate must be between 0 and 100");
        }
        this.discountRate = discountRate;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public VfpDiscountInPercentPerk(VfpDiscountInPercentPerkDTO dto) {
        this(dto.discountRate(), dto.startHour(), dto.endHour());
        this.setPerkId(dto.perkId());
    }

    @Override
    public boolean isDiscountPerk() {
        return true;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public LocalTime getStartHour() {
        return startHour;
    }

    public LocalTime getEndHour() {
        return endHour;
    }

    @Override
    public String toString() {
        return discountRate + "% de réduction pour tous les VFP lors de la réservation entre " + startHour + "h et " + endHour + "h";
    }

    @Override
    public <T> T accept(PerkVisitor<T> visitor) {
        return visitor.toDTO(this);
    }

    @Override
    public boolean apply(PerkApplicationVisitor visitor, Customer customer) throws ClosedTimeException, UnreachableExternalServiceException, BookingTimeNotSetException {
        return visitor.visit(this, customer);
    }

    @Override
    public boolean isConsumableFor(Customer customer) {
        if (customer.getCart() == null) {
            return false;
        }
        if (!customer.isVfp()) {
            return false;
        }
        List<CartItem> hkItems = customer.getCart().getHKItems();

        for (CartItem item : hkItems) {
            if (item.getStartTime() == null) {
                return false;
            }
            LocalTime bookingTime = item.getStartTime().toLocalTime();
            if (!startHour.isAfter(endHour)) { // same-day interval
                return !bookingTime.isBefore(startHour) && bookingTime.isBefore(endHour);
            }
            return !bookingTime.isBefore(startHour) || bookingTime.isBefore(endHour);
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VfpDiscountInPercentPerk that = (VfpDiscountInPercentPerk) o;
        return Objects.equals(this.getPerkId(), that.getPerkId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getPerkId());
    }
}
