package fr.univcotedazur.teamj.kiwicard.entities.perks;


import fr.univcotedazur.teamj.kiwicard.entities.Item;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
public class NPurchasedMGiftedPerk extends AbstractPerk{

    @NotNull
    @Column
    private int nbPurchased;

    @NotNull
    @Column
    private int nbGifted;

    @OneToOne
    private Item item;

    public NPurchasedMGiftedPerk() {
    }

    public NPurchasedMGiftedPerk(String name, String description, LocalDateTime startDate, LocalDateTime endDate, int nbPurchased, int nbGifted) {
        super();
        this.nbPurchased = nbPurchased;
        this.nbGifted = nbGifted;
    }

    @NotNull
    public int getNbPurchased() {
        return nbPurchased;
    }

    public void setNbPurchased(@NotNull int nbPurchased) {
        this.nbPurchased = nbPurchased;
    }

    @NotNull
    public int getNbGifted() {
        return nbGifted;
    }

    public void setNbGifted(@NotNull int nbGifted) {
        this.nbGifted = nbGifted;
    }
}
