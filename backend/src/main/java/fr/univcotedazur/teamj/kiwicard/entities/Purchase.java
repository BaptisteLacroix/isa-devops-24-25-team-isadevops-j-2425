package fr.univcotedazur.teamj.kiwicard.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Purchase {


    @Id
    @GeneratedValue
    private Long purchaseId;

    public Purchase() {
    }

    public Long getPurchaseId() {
        return purchaseId;
    }


    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }


}
