package fr.univcotedazur.teamj.kiwicard.entities.perks;

import fr.univcotedazur.teamj.kiwicard.entities.Partner;
import fr.univcotedazur.teamj.kiwicard.mappers.PerkVisitor;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractPerk {

    @Id
    @GeneratedValue
    private Long perkId;

    @ManyToOne
    @JoinColumn(name = "partner_id")
    private Partner partner;

    protected AbstractPerk() {
    }

    public Long getPerkId() {
        return perkId;
    }
    public abstract <T> T accept(PerkVisitor<T> visitor);
}
