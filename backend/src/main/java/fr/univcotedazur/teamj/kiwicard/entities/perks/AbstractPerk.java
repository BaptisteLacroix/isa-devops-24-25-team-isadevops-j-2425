package fr.univcotedazur.teamj.kiwicard.entities.perks;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public abstract class AbstractPerk {

    @Id
    @GeneratedValue
    private Long perkId;

}
