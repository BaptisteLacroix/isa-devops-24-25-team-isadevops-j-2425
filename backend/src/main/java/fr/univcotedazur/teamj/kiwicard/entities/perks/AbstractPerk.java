package fr.univcotedazur.teamj.kiwicard.entities.perks;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractPerk {

    @Id
    @GeneratedValue
    private Long perkId;

    public AbstractPerk() {
    }

    public Long getPerkId() {
        return perkId;
    }

    public void setPerkId(Long perkId) {
        this.perkId = perkId;
    }
}
