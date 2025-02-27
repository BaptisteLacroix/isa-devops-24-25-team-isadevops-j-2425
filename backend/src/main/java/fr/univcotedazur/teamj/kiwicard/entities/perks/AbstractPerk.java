package fr.univcotedazur.teamj.kiwicard.entities.perks;

import fr.univcotedazur.teamj.kiwicard.entities.Customer;
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

    private PerkType perkType;

    protected AbstractPerk() {
    }

    protected AbstractPerk(PerkType perkType) {
        this.perkType = perkType;
    }

    public Long getPerkId() {
        return perkId;
    }

    public void setPerkId(Long perkId) {
        this.perkId = perkId;
    }

    public PerkType getPerkType() {
        return perkType;
    }

    /**
     * Tente d’appliquer le perk sur le panier du client.
     * La méthode vérifie si les conditions sont remplies puis modifie le panier en conséquence.
     *
     * @param customer le client auquel le perk est destiné
     * @return true si le perk a été appliqué, false sinon.
     */
    public abstract boolean apply(Customer customer);

    public abstract boolean consumable(Customer customer);

    public abstract <T> T accept(PerkVisitor<T> visitor);
}
