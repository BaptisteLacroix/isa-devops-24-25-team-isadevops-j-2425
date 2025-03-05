package fr.univcotedazur.teamj.kiwicard.entities.perks;


import fr.univcotedazur.teamj.kiwicard.entities.Item;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;

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

    public NPurchasedMGiftedPerk(int nbPurchased, int nbGifted, Item item) {
        super();
        this.nbPurchased = nbPurchased;
        this.nbGifted = nbGifted;
        this.item = item;
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

    @Override
    public String toString() {
        return "Buy " + nbPurchased + " " + item.getLabel() + " and get " + nbGifted + " for free";
    }
}
