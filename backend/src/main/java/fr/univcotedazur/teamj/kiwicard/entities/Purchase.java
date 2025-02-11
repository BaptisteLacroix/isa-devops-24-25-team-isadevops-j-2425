package fr.univcotedazur.teamj.kiwicard.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Purchase {


    @Id
    @GeneratedValue
    private Long purchaseId;

    @OneToOne
    private Payment payment;

    @OneToOne
    private Cart cart;

    @NotNull
    public boolean isAlreadyConsumedInAPerk() {
        return alreadyConsumedInAPerk;
    }

    public void setAlreadyConsumedInAPerk(@NotNull boolean alreadyConsumedInAPerk) {
        this.alreadyConsumedInAPerk = alreadyConsumedInAPerk;
    }

    @NotNull
    @Column
    private boolean alreadyConsumedInAPerk;

    public Purchase() {
    }

    public Purchase(Payment payment, Cart cart, boolean alreadyConsumedInAPerk) {
        this.payment = payment;
        this.cart = cart;
        this.alreadyConsumedInAPerk = alreadyConsumedInAPerk;
    }

    public Long getPurchaseId() {
        return purchaseId;
    }


    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }


}
