package fr.univcotedazur.teamj.kiwicard.controllers;

import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.exceptions.NegativeQuantityException;
import fr.univcotedazur.teamj.kiwicard.interfaces.CartModifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = CustomerCareController.BASE_URI, produces = APPLICATION_JSON_VALUE)
// referencing the same BASE_URI as Customer care to extend it hierarchically
public class CartController {

    public static final String CART_URI = "/{customerId}/cart";

    private final CartModifier cart;

    @Autowired
    public CartController(CartModifier cart) {
        this.cart = cart;
    }
}
