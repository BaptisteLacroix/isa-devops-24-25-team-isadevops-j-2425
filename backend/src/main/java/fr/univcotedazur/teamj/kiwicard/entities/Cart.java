package fr.univcotedazur.teamj.kiwicard.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;


@Entity
public class Cart {

    @Id
    @GeneratedValue
    private Long cartId;

    public Cart() {
    }


    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }
}
