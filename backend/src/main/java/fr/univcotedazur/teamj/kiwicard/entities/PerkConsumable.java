package fr.univcotedazur.teamj.kiwicard.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity
public abstract class PerkConsumable {

    @NotBlank
    @Column
    private boolean alreadyConsumedInAPerk;

    @Id
    private Long id;

}
