package fr.univcotedazur.teamj.kiwicard.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity
public abstract class PerkConsumable {



    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    @Column
    private boolean alreadyConsumedInAPerk;

    public PerkConsumable() {
    }

    public PerkConsumable(boolean alreadyConsumedInAPerk) {
        this.alreadyConsumedInAPerk = alreadyConsumedInAPerk;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotBlank
    public boolean isAlreadyConsumedInAPerk() {
        return alreadyConsumedInAPerk;
    }

    public void setAlreadyConsumedInAPerk(@NotBlank boolean alreadyConsumedInAPerk) {
        this.alreadyConsumedInAPerk = alreadyConsumedInAPerk;
    }
}
