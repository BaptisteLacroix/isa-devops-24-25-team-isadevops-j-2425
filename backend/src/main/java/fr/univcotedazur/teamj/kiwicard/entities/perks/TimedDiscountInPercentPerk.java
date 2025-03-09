package fr.univcotedazur.teamj.kiwicard.entities.perks;

import fr.univcotedazur.teamj.kiwicard.dto.perks.TimedDiscountInPercentPerkDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.mappers.PerkVisitor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

@Entity
public class TimedDiscountInPercentPerk extends AbstractPerk{

    /**
     * The time after which the discount can be applied
     */
    @NotNull
    @Column
    private LocalTime time;

    @NotNull
    @Column
    private double discountRate;

    public TimedDiscountInPercentPerk() {
    }

    public TimedDiscountInPercentPerk(LocalTime time, double discountRate) {
        this.time = time;
        this.discountRate = discountRate;
    }

    public TimedDiscountInPercentPerk(TimedDiscountInPercentPerkDTO dto) {
        this.setPerkId(dto.perkId());
        this.time = dto.time();
        this.discountRate = dto.discountRate();
    }

    public @NotNull LocalTime getTime() {
        return time;
    }

    public void setTime(@NotNull LocalTime time) {
        this.time = time;
    }

    @NotNull
    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(@NotNull double quantity) {
        this.discountRate = quantity;
    }

    @Override
    public String toString() {
        return "Discount of " + discountRate + "% after " + time + "on all items";
    }

    @Override
    public boolean apply(Customer customer) {
        if (!LocalTime.now().isAfter(time)) {
            return false;
        }
        if (customer.getCart() == null) {
            throw new IllegalStateException("Customer has no cart");
        }
        customer.getCart().addToTotalPercentageReduction(discountRate);
        return true;
    }

    @Override
    public boolean isConsumableFor(Customer customer) {
        return LocalTime.now().isAfter(time);
    }

    @Override
    public <T> T accept(PerkVisitor<T> visitor) {
        return visitor.toDTO(this);
    }
}
