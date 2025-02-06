package fr.univcotedazur.teamj.kiwicard.entities.perks;


import fr.univcotedazur.teamj.kiwicard.entities.Item;
import jakarta.persistence.Column;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class NPurchasedMGiftedPerk extends AbstractPerk{

    @NotBlank
    @Column
    private int nbPurchased;

    @NotBlank
    @Column
    private int nbGifted;

    @OneToOne
    @Column
    private Item item;

    public NPurchasedMGiftedPerk() {
    }

    public NPurchasedMGiftedPerk(String name, String description, LocalDateTime startDate, LocalDateTime endDate, int nbPurchased, int nbGifted) {
        super();
        this.nbPurchased = nbPurchased;
        this.nbGifted = nbGifted;
    }

    @NotBlank
    public int getNbPurchased() {
        return nbPurchased;
    }

    public void setNbPurchased(@NotBlank int nbPurchased) {
        this.nbPurchased = nbPurchased;
    }

    @NotBlank
    public int getNbGifted() {
        return nbGifted;
    }

    public void setNbGifted(@NotBlank int nbGifted) {
        this.nbGifted = nbGifted;
    }
}
