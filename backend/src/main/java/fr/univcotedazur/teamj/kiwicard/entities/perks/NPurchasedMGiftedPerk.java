package fr.univcotedazur.teamj.kiwicard.entities.perks;


import fr.univcotedazur.teamj.kiwicard.dto.perks.NPurchasedMGiftedPerkDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.CartItem;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.mappers.PerkVisitor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class NPurchasedMGiftedPerk extends AbstractPerk {

    @NotNull
    @Column
    private int nbPurchased;

    @NotNull
    @Column
    private int nbGifted;

    @OneToOne
    private Item item;

    public NPurchasedMGiftedPerk() {
        super(PerkType.INTERMEDIATE);
    }

    public NPurchasedMGiftedPerk(int nbPurchased, int nbGifted, Item item) {
        this();
        this.nbPurchased = nbPurchased;
        this.nbGifted = nbGifted;
        this.item = item;
    }

    public NPurchasedMGiftedPerk(NPurchasedMGiftedPerkDTO perkDTO) {
        this();
        this.setPerkId(perkDTO.perkId());
        this.nbPurchased = perkDTO.nbPurchased();
        this.nbGifted = perkDTO.nbGifted();
        this.item = new Item(perkDTO.item());
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

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    private boolean isEligibleForGift(CartItem cartItem) {
        return cartItem.getQuantity() >= nbPurchased;
    }

    @Override
    public boolean apply(Customer customer) {
        Cart cart = customer.getCart();
        CartItem cartItem = cart.getItemById(this.item.getItemId());
        if (isEligibleForGift(cartItem)) {
            cartItem.increaseQuantity(nbGifted);
            return true;
        }
        return false;
    }

    @Override
    public boolean isConsumableFor(Customer customer) {
        Cart cart = customer.getCart();
        CartItem cartItem = cart.getItemById(this.item.getItemId());
        return isEligibleForGift(cartItem);
    }

    @Override
    public <T> T accept(PerkVisitor<T> visitor) {
        return visitor.toDTO(this);
    }

    @Override
    public String toString() {
        return "Buy " + nbPurchased + " " + item.getLabel() + " and get " + nbGifted + " for free";
    }
}
