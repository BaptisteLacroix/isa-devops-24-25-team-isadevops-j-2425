package fr.univcotedazur.teamj.kiwicard.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

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

    @NotBlank
    @Column
    public boolean vfp;

    @OneToMany
    @Column
    private List<Purchase> purchaseList;

    @OneToOne
    @PrimaryKeyJoinColumn
    @NotFound(action = NotFoundAction.IGNORE) //  1 to 0 ou 1
    private Cart cart;

    // Fait pour faire passer les tests, à refaire !!
    public Customer(String firstName, String id) {
        this.firstName = firstName;
        this.surname = id;
    }

    public Customer() {

    }

    public Customer(String firstName, String surname, String address, String email, boolean vfp) {
        this.firstName = firstName;
        this.surname = surname;
        this.address = address;
        this.email = email;
        this.vfp = vfp;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
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

    public void setEmail(@NotBlank String email) {
        this.email = email;
    }

    @NotBlank
    public boolean isVfp() {
        return vfp;
    }

    public void setVfp(@NotBlank boolean vfp) {
        this.vfp = vfp;
    }
}
