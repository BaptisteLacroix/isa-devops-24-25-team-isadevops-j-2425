package fr.univcotedazur.teamj.kiwicard.entities;

import fr.univcotedazur.teamj.kiwicard.dto.CustomerSubscribeDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CustomerDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Customer {

    @Id
    @NotBlank
    @Column
    private String email;

    @NotBlank
    @Column
    private String cardNumber;

    @NotBlank
    @Column
    private String firstName;

    @NotBlank
    @Column
    private String surname;

    @NotBlank
    @Column
    private String address;

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

    public Customer(CustomerDTO customerDTO) {
        this.email = customerDTO.email();
        this.firstName = customerDTO.firstName();
        this.surname = customerDTO.surname();
        this.vfp = customerDTO.vfp();
    }

    // Fait pour faire passer les tests, à refaire !!
    public Customer(String firstName, String email) {
        this.firstName = firstName;
        this.email = email;
    }

    public Customer(CustomerSubscribeDTO customerSubscribeDTO, String cardNumber) {
        this.firstName = customerSubscribeDTO.firstName();
        this.surname = customerSubscribeDTO.surname();
        this.address = customerSubscribeDTO.address();
        this.email = customerSubscribeDTO.email();
        this.vfp = false;
        // TODO : ajouter un numéro de carte via CardEditorProxy
        this.cardNumber = cardNumber;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(email, customer.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }
}
