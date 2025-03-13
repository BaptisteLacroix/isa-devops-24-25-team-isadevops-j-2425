package fr.univcotedazur.teamj.kiwicard.entities;


import jakarta.persistence.*;
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

    @ManyToOne
    @JoinColumn(name = "partner_id")
    private Partner partner;

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

    public void setPartner(Partner partner) {
        this.partner = partner;
    }
}
