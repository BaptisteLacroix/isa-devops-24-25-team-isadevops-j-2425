package fr.univcotedazur.teamj.kiwicard.entities.perks;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
public class TimedDiscountInPercentPerk extends AbstractPerk{

    @NotNull
    @Column
    private LocalDateTime time;

    @NotNull
    @Column
    private double quantity;

    public TimedDiscountInPercentPerk() {
    }

    public TimedDiscountInPercentPerk(LocalDateTime time, double quantity) {
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
