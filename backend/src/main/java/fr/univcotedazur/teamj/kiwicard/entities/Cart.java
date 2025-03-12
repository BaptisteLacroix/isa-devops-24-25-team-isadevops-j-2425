package fr.univcotedazur.teamj.kiwicard.entities;

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
import org.springframework.beans.factory.annotation.Value;

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
    private List<AbstractPerk> perksToUse = new ArrayList<>();

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

    public Cart(Partner partner, Set<CartItem> itemList, List<AbstractPerk> perks) {
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

    public List<CartItem> getHKItems(@Value("${happykids.item.name}") String itemName) {
        return this.itemList.stream()
                .filter(item -> item.getItem().getLabel().contains(itemName))
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

    public Set<CartItem> getItemList() {
        return itemList;
    }

    public boolean isEmpty() {
        return this.itemList.isEmpty();
    }

    public void empty() {
        this.itemList.clear();
    }

    public List<AbstractPerk> getPerksToUse() {
        return perksToUse;
    }

    public double getTotalPercentageReduction() {
        return totalPercentageReduction;
    }

    public double addToTotalPercentageReduction(double amount) {
        this.totalPercentageReduction += amount;
        return this.totalPercentageReduction;
    }

    public double resetTotalPercentageReduction() {
        this.totalPercentageReduction = 0;
        return this.totalPercentageReduction;
    }

    public double getTotalPrice() {
        return getItemList().stream().mapToDouble(CartItem::getPrice).sum();
    }
}
