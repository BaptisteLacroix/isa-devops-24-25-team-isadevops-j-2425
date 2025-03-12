package fr.univcotedazur.teamj.kiwicard.entities.perks;

import fr.univcotedazur.teamj.kiwicard.dto.perks.VfpDiscountInPercentPerkDTO;
import fr.univcotedazur.teamj.kiwicard.entities.CartItem;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.exceptions.ClosedTimeException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
import fr.univcotedazur.teamj.kiwicard.mappers.PerkVisitor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.List;

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
        while (discountRate > 1) {
            discountRate = discountRate / 100;
        }
        this.discountRate = discountRate;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public VfpDiscountInPercentPerk(VfpDiscountInPercentPerkDTO dto) {
        this(dto.discountRate(), dto.startHour(), dto.endHour());
        this.setPerkId(dto.perkId());
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double percentage) {
        this.discountRate = percentage;
    }

    public LocalTime getStartHour() {
        return startHour;
    }

    public void setStartHour(LocalTime startHour) {
        this.startHour = startHour;
    }

    public LocalTime getEndHour() {
        return endHour;
    }

    public void setEndHour(LocalTime endHour) {
        this.endHour = endHour;
    }

    @Override
    public String toString() {
        return discountRate + "% discount for all VFPs when booking between " + startHour + "h and " + endHour + "h";
    }

    @Override
    public <T> T accept(PerkVisitor<T> visitor) {
        return visitor.toDTO(this);
    }

    @Override
    public boolean apply(PerkApplicationVisitor visitor) throws ClosedTimeException, UnreachableExternalServiceException {
        return visitor.visit(this);
    }

    @Override
    public boolean isConsumableFor(Customer customer) {
        if (customer.getCart() == null) {
            return false;
        }
        List<CartItem> hkItems = customer.getCart().getHKItems(null);

        for (CartItem item : hkItems) {
            if (item.getStartTime() == null) {
                return false;
            }
            int bookingHour = item.getStartTime().getHour();
            if (customer.isVfp() && bookingHour >= startHour.getHour() && bookingHour < endHour.getHour()) {
                return true;
            }
        }
        return false;
    }
}
