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
}
