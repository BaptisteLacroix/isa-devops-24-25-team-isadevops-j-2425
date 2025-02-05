package fr.univcotedazur.teamj.kiwicard.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

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
}
