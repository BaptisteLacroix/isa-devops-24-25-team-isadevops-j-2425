package fr.univcotedazur.teamj.kiwicard.entities;

import fr.univcotedazur.teamj.kiwicard.dto.CartItemDTO;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class CartItem {

    @Id
    @GeneratedValue
    private Long cartItemId;

    @Column
    private int quantity;

    @Column
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "cart_id", insertable = false, updatable = false)
    private Cart cart;

    @ManyToOne(cascade = CascadeType.ALL)
    private Item item;
    public CartItem() {
    }

    public CartItem(Item item, int quantity, LocalDateTime startTime, LocalDateTime endTime) {
        this.item = item;
        this.quantity = quantity;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public CartItem(CartItemDTO cartItemDTO) {
        this.cartItemId = cartItemDTO.cartItemId();
        this.quantity = cartItemDTO.quantity();
        this.startTime = cartItemDTO.startTime();
        this.endTime = cartItemDTO.endTime();
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Long getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(Long cartItemId) {
        this.cartItemId = cartItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Item getItem() {
        return item;
    }
}

