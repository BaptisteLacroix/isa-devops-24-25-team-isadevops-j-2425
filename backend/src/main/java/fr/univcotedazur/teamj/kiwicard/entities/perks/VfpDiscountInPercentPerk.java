package fr.univcotedazur.teamj.kiwicard.entities.perks;

import fr.univcotedazur.teamj.kiwicard.dto.perks.VfpDiscountInPercentPerkDTO;
import fr.univcotedazur.teamj.kiwicard.entities.CartItem;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.mappers.PerkVisitor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class VfpDiscountInPercentPerk extends AbstractPerk {
    @Column
    @NotNull
    private double discountRate;

    @Column
    @NotNull
    private LocalDateTime startHour;

    @Column
    @NotNull
    private LocalDateTime endHour;


    public VfpDiscountInPercentPerk() {
    }

    public VfpDiscountInPercentPerk(double discountRate, LocalDateTime startHour, LocalDateTime endHour) {
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

    public LocalDateTime getStartHour() {
        return startHour;
    }

    public void setStartHour(LocalDateTime startHour) {
        this.startHour = startHour;
    }

    public LocalDateTime getEndHour() {
        return endHour;
    }

    public void setEndHour(LocalDateTime endHour) {
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
    public boolean apply(Customer customer) {
        if (!customer.isVfp()) {
            return false;
        }
        if (customer.getCart() == null) {
            throw new IllegalStateException("Customer has no cart");
        }
        return true;
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
