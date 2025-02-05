package fr.univcotedazur.teamj.kiwicard.entities.perks;


import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class NPurchasedMGiftedPerk extends AbstractPerk{

    @NotBlank
    @Column
    private int nbPurchased;

    @NotBlank
    @Column
    private int nbGifted;
}
