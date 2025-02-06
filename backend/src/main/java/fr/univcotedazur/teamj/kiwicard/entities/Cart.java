package fr.univcotedazur.teamj.kiwicard.entities;

import fr.univcotedazur.teamj.kiwicard.entities.perks.AbstractPerk;
import jakarta.persistence.*;

import java.util.List;


@Entity
public class Cart {

    @Id
    @GeneratedValue
    private Long cartId;

    public Cart() {
    }

    @ManyToOne
    @JoinColumn
    private Partner partner;

    @ManyToMany
    @Column
    private List<AbstractPerk> perksList;




    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }
}
