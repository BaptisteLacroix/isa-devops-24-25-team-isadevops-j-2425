package fr.univcotedazur.teamj.kiwicard.entities.perks;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class TimedDiscountInPercentPerk extends AbstractPerk{

    @NotBlank
    @Column
    private LocalDateTime time;

    @NotBlank
    @Column
    private double quantity;

    public TimedDiscountInPercentPerk() {
    }

    public TimedDiscountInPercentPerk(LocalDateTime time, double quantity) {
        this.time = time;
        this.quantity = quantity;
    }

    public @NotBlank LocalDateTime getTime() {
        return time;
    }

    public void setTime(@NotBlank LocalDateTime time) {
        this.time = time;
    }

    @NotBlank
    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(@NotBlank double quantity) {
        this.quantity = quantity;
    }


}
