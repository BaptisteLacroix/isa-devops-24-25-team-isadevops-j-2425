package fr.univcotedazur.teamj.kiwicard.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Purchase {

    @Id
    @GeneratedValue
    private Long purchaseId;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cart_id", unique = true)
    private Cart cart;

    @NotNull
    @Column
    private boolean alreadyConsumedInAPerk;

    public Purchase() {
    }

    public Purchase(Payment payment, Cart cart) {
        this.payment = payment;
        this.cart = cart;
        this.alreadyConsumedInAPerk = false;
    }

    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }


    @NotNull
    public boolean isAlreadyConsumedInAPerk() {
        return alreadyConsumedInAPerk;
    }

    public void setAlreadyConsumedInAPerk(@NotNull boolean alreadyConsumedInAPerk) {
        this.alreadyConsumedInAPerk = alreadyConsumedInAPerk;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
