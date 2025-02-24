package fr.univcotedazur.teamj.kiwicard.entities;


import fr.univcotedazur.teamj.kiwicard.entities.perks.AbstractPerk;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Collection;
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

    @OneToMany
    @Column
    private List<Item> itemList;

    public Partner() {
    }

    public Partner(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
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

    public List<Purchase> getPurchases() {
        return this.purchaseList;
    }
}
