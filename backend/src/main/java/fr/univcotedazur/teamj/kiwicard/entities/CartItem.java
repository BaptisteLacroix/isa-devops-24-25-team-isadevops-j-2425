package fr.univcotedazur.teamj.kiwicard.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

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

    public CartItem() {
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
}

