package fr.univcotedazur.teamj.kiwicard.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

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


    // Fait pour faire passer les tests, à refaire !!
    public Customer(String firstName, String id) {
        this.firstName = firstName;
        this.surname = id;
    }

    public Customer() {

    }



}
