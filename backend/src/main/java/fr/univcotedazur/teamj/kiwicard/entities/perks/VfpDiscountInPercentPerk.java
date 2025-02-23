package fr.univcotedazur.teamj.kiwicard.entities.perks;

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
    }

    public VfpDiscountInPercentPerk(double discountRate) {
        while (discountRate >1){
            discountRate = discountRate/100;
        }
        this.discountRate = discountRate;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double percentage) {
        this.discountRate = percentage;
    }

    @Override
    public String toString() {
        return  discountRate + "% discount for all VFPs";
    }

    @Override
    public <T> T accept(PerkVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
