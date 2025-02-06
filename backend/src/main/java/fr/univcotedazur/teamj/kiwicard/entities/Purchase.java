package fr.univcotedazur.teamj.kiwicard.entities;


import jakarta.persistence.*;

@Entity
public class Purchase {


    @Id
    @GeneratedValue
    private Long purchaseId;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Payment payment;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Cart cart;

    public Purchase() {
    }

    public Long getPurchaseId() {
        return purchaseId;
    }


    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }


}
