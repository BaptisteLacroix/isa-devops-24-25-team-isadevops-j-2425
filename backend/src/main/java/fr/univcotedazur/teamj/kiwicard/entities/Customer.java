package fr.univcotedazur.teamj.kiwicard.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Customer {

    @Id
    @GeneratedValue
    private Long customerId; // Whether Long/Int or UUID are better primary keys, exposable outside is a vast issue, keep it simple here

    @NotBlank
    @Column(unique = true)
    private String firstName;

    @NotBlank
    @Column(unique = true)
    private String surname;

    @NotBlank
    @Column(unique = true)
    private String address;

    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    @Column(unique = true)
    public boolean vfp;

    @Pattern(regexp = "\\d{10}+", message = "Invalid creditCardNumber")
    private String creditCard;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Item> cart = new HashSet<>();





}
