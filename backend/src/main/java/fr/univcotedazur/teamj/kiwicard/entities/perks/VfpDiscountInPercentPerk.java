package fr.univcotedazur.teamj.kiwicard.entities.perks;

import fr.univcotedazur.teamj.kiwicard.dto.perks.VfpDiscountInPercentPerkDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.mappers.PerkVisitor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

@Entity
public class VfpDiscountInPercentPerk extends AbstractPerk {
    @Column
    @NotNull
    private double discountRate;

    public VfpDiscountInPercentPerk() {
        super(PerkType.FINAL);
    }

    public VfpDiscountInPercentPerk(double discountRate) {
        this();
        while (discountRate > 1) {
            discountRate = discountRate / 100;
        }
        this.discountRate = discountRate;
    }

    public VfpDiscountInPercentPerk(VfpDiscountInPercentPerkDTO dto) {
        this(dto.discountRate());
        this.setPerkId(dto.perkId());
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double percentage) {
        this.discountRate = percentage;
    }

    @Override
    public String toString() {
        return discountRate + "% discount for all VFPs";
    }

    @Override
    public <T> T accept(PerkVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean apply(Customer customer) {
        if (!customer.isVfp()) {
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
        return customer.isVfp();
    }
}
