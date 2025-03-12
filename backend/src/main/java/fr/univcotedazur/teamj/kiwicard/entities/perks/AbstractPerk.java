package fr.univcotedazur.teamj.kiwicard.entities.perks;

import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.entities.Partner;
import fr.univcotedazur.teamj.kiwicard.exceptions.ClosedTimeException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
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

    protected void setPerkId(Long perkId) {
        this.perkId = perkId;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    /**
     * Tente d’appliquer le perk sur le panier du client.
     * La méthode vérifie si les conditions sont remplies puis modifie le panier en conséquence.
     *
     * @param visitor le visiteur qui va appliquer le perk
     * @return true si le perk a été appliqué, false sinon.
     */
    public abstract boolean apply(PerkApplicationVisitor visitor) throws ClosedTimeException, UnreachableExternalServiceException;

    public abstract boolean isConsumableFor(Customer customer);

    public abstract <T> T accept(PerkVisitor<T> visitor);
}
