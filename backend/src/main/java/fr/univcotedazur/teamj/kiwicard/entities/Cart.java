package fr.univcotedazur.teamj.kiwicard.entities;

import fr.univcotedazur.teamj.kiwicard.configurations.Constants;
import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.entities.perks.AbstractPerk;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Cart {

    @Id
    @GeneratedValue
    private Long cartId;

    @ManyToOne
    @JoinColumn
    private Partner partner;

    @NotNull
    private double totalPercentageReduction;

    /**
     * The list of perks that can be applied to the cart
     */
    @ManyToMany
    @Column
    private Set<AbstractPerk> perksToUse = new HashSet<>();

    /**
     * The list of perks that have been applied to the cart
     */
    @ManyToMany
    @Column
    private List<AbstractPerk> perksUsed = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "cart_id")
    private Set<CartItem> itemList = new HashSet<>();

    public Cart() {
    }

    public Cart(CartDTO cartDTO) {
        this.cartId = cartDTO.cartId();
    }

    public Cart(Partner partner, Set<CartItem> itemList, Set<AbstractPerk> perks) {
        this.partner = partner;
        this.itemList = itemList;
        this.perksToUse = perks;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public Long getCartId() {
        return cartId;
    }

    public void addPerkToUse(AbstractPerk perk) {
        this.perksToUse.add(perk);
    }

    public void addItem(CartItem item) {
        this.itemList.add(item);
    }

    public List<CartItem> getHKItems() {
        return this.itemList.stream()
                .filter(item -> item.getItem().getLabel().contains(Constants.HAPPY_KIDS_ITEM_NAME))
                .toList();

    }

    public CartItem getItemById(Long itemId) {
        for (CartItem cartItem : itemList) {
            if (cartItem.getItem().getItemId().equals(itemId)) {
                return cartItem;
            }
        }
        return null;
    }

    public Partner getPartner() {
        return partner;
    }

    public Set<CartItem> getItems() {
        return itemList;
    }

    public boolean isEmpty() {
        return this.itemList.isEmpty();
    }

    public Set<AbstractPerk> getPerksToUse() {
        return perksToUse;
    }

    public List<AbstractPerk> getPerksUsed() {
        return perksUsed;
    }

    public double getTotalPercentageReduction() {
        return totalPercentageReduction;
    }

    public double addToTotalPercentageReduction(double amount) {
        this.totalPercentageReduction += amount;
        return this.totalPercentageReduction;
    }

    public double getTotalPrice() {
        return this.getItems().stream().mapToDouble(CartItem::getPrice).sum();
    }

    public boolean alreadyContains(Item item) {
        return this.getItems().stream().map(CartItem::getItem).anyMatch(itm -> itm.equals(item));
    }

    public void addPerkUsed(AbstractPerk perk) {
        this.perksUsed.add(perk);
    }
}
