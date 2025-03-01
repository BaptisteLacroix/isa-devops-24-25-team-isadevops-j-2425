package fr.univcotedazur.teamj.kiwicard.entities;


import fr.univcotedazur.teamj.kiwicard.dto.PartnerCreationDTO;
import fr.univcotedazur.teamj.kiwicard.entities.perks.AbstractPerk;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Partner {

    @Id
    @GeneratedValue
    private Long partnerId;

    @NotBlank
    @Column
    private String name;

    @NotBlank
    @Column
    private String address;

    @OneToMany
    @Column
    private List<Purchase> purchaseList;

    @OneToMany
    @Column
    private List<AbstractPerk> perkList;

    @OneToMany(cascade = CascadeType.ALL)
    @Column
    private List<Item> itemList;

    public Partner() {
    }

    public Partner(String name, String address) {
        this.name = name;
        this.address = address;
        this.itemList = new ArrayList<>();
        this.perkList = new ArrayList<>();
        this.purchaseList = new ArrayList<>();
    }
    public Partner(PartnerCreationDTO partnerDTO) {
        this(partnerDTO.name(), partnerDTO.address());
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public @NotBlank String getName() {
        return name;
    }

    public void setName(@NotBlank String name) {
        this.name = name;
    }

    public @NotBlank String getAddress() {
        return address;
    }

    public void setAddress(@NotBlank String address) {
        this.address = address;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public List<AbstractPerk> getPerkList() {
        return perkList;
    }

    public List<Purchase> getPurchaseList() {
        return purchaseList;
    }

    public void addItem(Item item) {
        itemList.add(item);
    }

    public void addPerk(AbstractPerk perk) {
        perkList.add(perk);
    }

    public void addPurchase(Purchase purchase) {
        purchaseList.add(purchase);
    }
}
