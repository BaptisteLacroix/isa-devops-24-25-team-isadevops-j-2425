package fr.univcotedazur.teamj.kiwicard.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Purchase {


    @Id
    @GeneratedValue
    private Long purchaseId;

    @OneToOne
    private Payment payment;

    @OneToOne
    private Cart cart;

    @NotBlank
    public boolean isAlreadyConsumedInAPerk() {
        return alreadyConsumedInAPerk;
    }

    public void setAlreadyConsumedInAPerk(@NotBlank boolean alreadyConsumedInAPerk) {
        this.alreadyConsumedInAPerk = alreadyConsumedInAPerk;
    }

    @NotBlank
    @Column
    private boolean alreadyConsumedInAPerk;

    public Purchase() {
    }

    public Long getPurchaseId() {
        return purchaseId;
    }


    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }


}
