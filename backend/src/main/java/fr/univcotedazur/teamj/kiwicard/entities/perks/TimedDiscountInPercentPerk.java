package fr.univcotedazur.teamj.kiwicard.entities.perks;

import fr.univcotedazur.teamj.kiwicard.dto.perks.TimedDiscountInPercentPerkDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.mappers.PerkVisitor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.Objects;

import static fr.univcotedazur.teamj.kiwicard.configurations.Constants.MAX_DISCOUNT_RATE_OF_A_PERK;
import static fr.univcotedazur.teamj.kiwicard.configurations.Constants.MIN_DISCOUNT_RATE_OF_A_PERK;

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
        if(discountRate > MAX_DISCOUNT_RATE_OF_A_PERK || discountRate < MIN_DISCOUNT_RATE_OF_A_PERK) {
            throw new IllegalArgumentException("Discount rate must be between 0 and 100");
        }
        this.discountRate = discountRate;
    }

    public TimedDiscountInPercentPerk(TimedDiscountInPercentPerkDTO dto) {
        this.setPerkId(dto.perkId());
        this.time = dto.time();
        this.discountRate = dto.discountRate();
    }

    @Override
    public boolean isDiscountPerk() {
        return true;
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
    @Override
    public String toString() {
        return "Discount of " + discountRate + "% after " + time + " on all items";
    }

    @Override
    public boolean apply(PerkApplicationVisitor visitor, Customer customer) {
        return visitor.visit(this, customer);
    }

    @Override
    public boolean isConsumableFor(Customer customer) {
        return LocalTime.now().isAfter(time);
    }

    @Override
    public <T> T accept(PerkVisitor<T> visitor) {
        return visitor.toDTO(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimedDiscountInPercentPerk that = (TimedDiscountInPercentPerk) o;
        return Objects.equals(this.getPerkId(), that.getPerkId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getPerkId());
    }
}
