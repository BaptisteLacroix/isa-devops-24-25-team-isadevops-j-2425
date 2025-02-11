package fr.univcotedazur.teamj.kiwicard.entities.perks;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
public class VfpDiscountInPercentPerk extends AbstractPerk{
    @NotNull
    @Column
    private LocalDateTime time;

    @NotNull
    @Column
    private double quantity;

    @Column
    @NotNull
    private final double percentage=0;

    public VfpDiscountInPercentPerk() {
    }

    public VfpDiscountInPercentPerk(@NotNull LocalDateTime time, @NotNull double quantity) {
        this.time = time;
        this.quantity = quantity;
    }

    public @NotNull LocalDateTime getTime() {
        return time;
    }

    public void setTime(@NotNull LocalDateTime time) {
        this.time = time;
    }

    @NotNull
    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(@NotNull double quantity) {
        this.quantity = quantity;
    }
}
