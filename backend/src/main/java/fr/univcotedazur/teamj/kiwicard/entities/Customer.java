package fr.univcotedazur.teamj.kiwicard.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Customer {

    @Id
    @GeneratedValue
    private Long customerId;

    @NotBlank
    @Column
    private String firstName;

    @NotBlank
    @Column
    private String surname;

    @NotBlank
    @Column
    private String address;

    @NotBlank
    @Column
    private String email;

    @NotNull
    @Column
    public boolean vfp;

    @OneToMany
    @Column
    private final List<Purchase> purchaseList = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cart_id", unique = true)
    private Cart cart;

    public Customer() {
    }

    public Customer(String firstName, String surname, String address, String email, boolean vfp) {
        this.firstName = firstName;
        this.surname = surname;
        this.address = address;
        this.email = email;
        this.vfp = vfp;
    }

    // Fait pour faire passer les tests, à refaire !!
    public Customer(String firstName, String email) {
        this.firstName = firstName;
        this.email = email;
    }



    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Cart getCart() {
        return cart;
    }

    public void removeCart() {
        this.cart = null;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void addPurchase(Purchase purchase) {
        this.purchaseList.add(purchase);
    }

    public @NotBlank String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotBlank String firstName) {
        this.firstName = firstName;
    }

    public @NotBlank String getSurname() {
        return surname;
    }

    public void setSurname(@NotBlank String surname) {
        this.surname = surname;
    }

    public @NotBlank String getAddress() {
        return address;
    }

    public void setAddress(@NotBlank String address) {
        this.address = address;
    }

    public @NotBlank String getEmail() {
        return email;
    }

    public void setEmail(@NotNull String email) {
        this.email = email;
    }

    @NotNull
    public boolean isVfp() {
        return vfp;
    }

    public void setVfp(@NotNull boolean vfp) {
        this.vfp = vfp;
    }

}
