package fr.univcotedazur.teamj.kiwicard.entities;

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

    private double price;

    @Column
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "cart_id", insertable = false, updatable = false)
    private Cart cart;

    @ManyToOne(cascade = CascadeType.ALL)
    private Item item;
    public void setItem(Item item) {
        this.item = item;
    }

    public CartItem() {
    }

    public CartItem(Item item, int quantity){
        this.item = item;
        this.quantity = quantity;
        this.price = item.getPrice() * quantity;
    }

    public CartItem(int quantity, LocalDateTime startTime, LocalDateTime endTime) {
        this.quantity = quantity;
        this.startTime = startTime;
        this.endTime = endTime;
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

    public void increaseQuantity(int quantity) {
        this.quantity += quantity;
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

    public double getPrice() {
        return price;
    }

}

