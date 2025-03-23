package fr.univcotedazur.teamj.kiwicard.entities;

import fr.univcotedazur.teamj.kiwicard.dto.CartItemAddDTO;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class CartItem {

    @Id
    @GeneratedValue
    private Long cartItemId;

    @Column
    private boolean consumed = false;

    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }

    @Column
    private int quantity;

    @Column
    private LocalDateTime startTime;

    public void setPrice(double price) {
        this.price = price;
    }

    private double price;

    @ManyToOne
    @JoinColumn(name = "cart_id", insertable = false, updatable = false)
    private Cart cart;

    @ManyToOne(cascade = CascadeType.ALL)
    private Item item;
    public CartItem() {
    }

    public CartItem(Item item, CartItemAddDTO cartItemAddDTO) {
        this.item = item;
        this.quantity = cartItemAddDTO.quantity();
        this.startTime = cartItemAddDTO.startTime();
        this.price = item.getPrice() * quantity;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Long getCartItemId() {
        return cartItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void addFreeItem(int quantity) {
        this.quantity += quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.price = item.getPrice() * this.quantity;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Item getItem() {
        return item;
    }

    public boolean isConsumed() {return this.consumed;}

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "cartItemId=" + cartItemId +
                ", quantity=" + quantity +
                ", startTime=" + startTime +
                ", item=" + item +
                '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof CartItem cartItem)) return false;

        return Objects.equals(getStartTime(), cartItem.getStartTime()) && Objects.equals(getItem(), cartItem.getItem());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getStartTime());
        result = 31 * result + Objects.hashCode(getItem());
        return result;
    }
}

