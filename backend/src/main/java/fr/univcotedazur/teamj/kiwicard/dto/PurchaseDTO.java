package fr.univcotedazur.teamj.kiwicard.dto;

import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.Payment;
import fr.univcotedazur.teamj.kiwicard.entities.Purchase;

public class PurchaseDTO {
    private Long purchaseId;
    private Payment payment;
    private Cart cart;
    private boolean alreadyConsumedInAPerk;

    public PurchaseDTO(Purchase purchase) {
        this.purchaseId = purchase.getPurchaseId();
        this.payment = purchase.getPayment();
        this.cart = purchase.getCart();
        this.alreadyConsumedInAPerk = purchase.isAlreadyConsumedInAPerk();
    }

    // Getters and setters
    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public boolean isAlreadyConsumedInAPerk() {
        return alreadyConsumedInAPerk;
    }

    public void setAlreadyConsumedInAPerk(boolean alreadyConsumedInAPerk) {
        this.alreadyConsumedInAPerk = alreadyConsumedInAPerk;
    }
}
