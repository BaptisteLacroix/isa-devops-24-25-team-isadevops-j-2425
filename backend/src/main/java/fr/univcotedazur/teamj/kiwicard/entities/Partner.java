package fr.univcotedazur.teamj.kiwicard.entities;


import fr.univcotedazur.teamj.kiwicard.dto.PartnerCreationDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PartnerDTO;
import fr.univcotedazur.teamj.kiwicard.entities.perks.AbstractPerk;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @OneToMany(mappedBy = "partner")
    @Column
    private List<Purchase> purchaseList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "partner")
    @Column
    private Set<AbstractPerk> perkSet;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "partner")
    @Column
    private List<Item> itemList;

    public Partner() {
    }

    public Partner(String name, String address) {
        this.name = name;
        this.address = address;
        this.itemList = new ArrayList<>();
        this.perkSet = new HashSet<>();
        this.purchaseList = new ArrayList<>();
    }

    public Partner(PartnerDTO partnerDTO) {
        this(partnerDTO.name(), partnerDTO.address());
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

    public Set<AbstractPerk> getPerkSet() {
        return perkSet;
    }

    public void addItem(Item item) {
        itemList.add(item);
        item.setPartner(this);
    }

    public void addPerk(AbstractPerk perk) {
        perk.setPartner(this);
        perkSet.add(perk);
    }

    public void addPurchase(Purchase purchase) {
        purchase.setPartner(this);
        purchaseList.add(purchase);
    }
}
