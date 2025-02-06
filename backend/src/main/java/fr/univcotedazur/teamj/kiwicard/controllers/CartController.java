package fr.univcotedazur.teamj.kiwicard.controllers;

import fr.univcotedazur.teamj.kiwicard.interfaces.cart.ICartModifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
//@RequestMapping(path = CustomerCareController.BASE_URI, produces = APPLICATION_JSON_VALUE)
// referencing the same BASE_URI as Customer care to extend it hierarchically
public class CartController {

    public static final String CART_URI = "/{customerId}/cart";

    private final ICartModifier cart;

    @Autowired
    public CartController(ICartModifier cart) {
        this.cart = cart;
    }
}
